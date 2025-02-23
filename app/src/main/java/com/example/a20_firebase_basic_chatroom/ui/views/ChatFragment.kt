package com.example.a20_firebase_basic_chatroom.ui.views

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.a20_firebase_basic_chatroom.R
import com.example.a20_firebase_basic_chatroom.applicationLevelFiles.TokenManager
import com.example.a20_firebase_basic_chatroom.data.api.messagesDao
import com.example.a20_firebase_basic_chatroom.data.dataModels.Message
import com.example.a20_firebase_basic_chatroom.data.dataModels.User
import com.example.a20_firebase_basic_chatroom.databinding.FragmentChatBinding
import com.example.a20_firebase_basic_chatroom.ui.viewModels.ChatViewModel
import com.example.a20_firebase_basic_chatroom.utils.Constants.ROOM_ID
import com.google.firebase.Timestamp
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ChatFragment : Fragment() {

    private lateinit var binding : FragmentChatBinding
    private lateinit var chatroomRef : DatabaseReference
    private val viewModel: ChatViewModel by viewModels()

    var isNewMsg = true
    private var roomID :String? = null
    lateinit var user : User            // Here "user" will be the current user, the person who is
                                // using this account, in this object we'll store user's name, id, etc..

    @Inject
    lateinit var tokenManager: TokenManager

    @Inject
    lateinit var dao : messagesDao

    private lateinit var adapter : ChatAdapter

    private var messagesListener: ChildEventListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        setupAdapter()
        listener()
        setupScrollListener()
    }

    @SuppressLint("SetTextI18n")
    private fun initView(){
        user = tokenManager.getUser()!!
        roomID = arguments?.getString(ROOM_ID)
        roomID?.let {roomID ->
            chatroomRef = FirebaseDatabase.getInstance().getReference("Chats").child(roomID)
        }
        binding.includeTopBar.tvRoomId.text = "Room- ${roomID ?: "Unknown Room"}"
    }

/*    private fun setupAdapter(){
        adapter = tokenManager.getUser()!!.userId?.let { ChatAdapter(it) }!!
        binding.rvChat.adapter = adapter
        binding.rvChat.layoutManager = LinearLayoutManager(requireContext()).apply {
            stackFromEnd = true
        }
    }*/

    private fun setupAdapter() {
        adapter = tokenManager.getUser()!!.userId?.let { ChatAdapter(it) }!!
        binding.rvChat.adapter = adapter
        binding.rvChat.layoutManager = LinearLayoutManager(requireContext()).apply {
            stackFromEnd = true
            reverseLayout = true  // Reverse layout for better pagination
        }
    }


    private fun listener(){

        // Fetch messages from firebase (Fetch only messages sent which are after last message in room-db)
        lifecycleScope.launch (Dispatchers.IO) {
            val timeStampOfLastMsgInRoom = dao.getLatestMsgTimestampOrZero(roomID!!)        // It returns timestamp of last message in that group which is present in room-db
            fetchAllMessagesFromFirebasePostThisTimestamp(timeStampOfLastMsgInRoom)         // We need to fetch messages from firebase, beyond that timestamp
        }

        // Listen to Firebase Realtime Database for new messages & update RoomDB
        val currentTime = System.currentTimeMillis().toDouble()
        messagesListener = chatroomRef.orderByChild("timestamp").startAfter(currentTime)
            .addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val newMessage = snapshot.getValue(Message::class.java)
                newMessage?.let {
                    lifecycleScope.launch (Dispatchers.IO){
                        if(it.roomId == null) it.roomId = roomID

                        dao.insertMessage(it) // Insert message into Room when it arrives from Firebase

                        val currentMessages = viewModel._messages.value ?: emptyList()

                        viewModel._messages.postValue(listOf(it) + currentMessages)
                        isNewMsg=true
                        Log.d("skt", it.toString())
                    }
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}
        })


/*
        dao.getChatsForThisRoom(roomID!!).observe(viewLifecycleOwner){response->

            val oldLastMessagePosition = adapter.itemCount - 1

            adapter.submitList(response) {
                binding.rvChat.scrollToPosition(adapter.itemCount - 1)

                // Ensure previous last message updates (To update the chat bubble of previous message
                if (oldLastMessagePosition >= 0) {
                    adapter.notifyItemChanged(oldLastMessagePosition)
                }
            }
        }
*/


        viewModel.messages.observe(viewLifecycleOwner) { messages ->
//            val oldLastMessagePosition = adapter.itemCount - 1

            adapter.submitList(messages) {
                // Only scroll to bottom for new messages, not loaded old ones
                if (viewModel.currentPage <= 1 || messages.size > adapter.itemCount || isNewMsg) {
                    binding.rvChat.scrollToPosition(0)
                    isNewMsg = false
                }

                // Update previous last message bubble
                if (adapter.itemCount > 1) {
                    adapter.notifyItemChanged(1)
                }
            }
        }



        binding.includeTextSender.btnSendMsg.setOnClickListener {
            val text = binding.includeTextSender.etTypeMessage.text.toString()      // here "text" will have the text msg

            val msgId = chatroomRef.push().key          // Each message should have a unique id,
                                                // This will generate that unique id

            msgId?.let {messageId ->
                val msg = user.userId?.let { senderId -> user.userName?.let { senderName ->
                    Message(msgId, text, senderId,
                        senderName, roomID
                    )
                } }
                chatroomRef.child(messageId).setValue(msg)

            } ?: run {
                Toast.makeText(requireContext(), "Error sending message", Toast.LENGTH_SHORT).show()
            }

            binding.includeTextSender.etTypeMessage.text.clear()
        }
    }


    private fun fetchAllMessagesFromFirebasePostThisTimestamp(timestamp: Long) {
        lifecycleScope.launch(Dispatchers.IO) {
            chatroomRef.orderByChild("timestamp").startAfter(timestamp.toDouble())
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val messages = mutableListOf<Message>()
                        for (child in snapshot.children) {
                            val msg = child.getValue(Message::class.java)
                            if(msg?.roomId == null){
                                msg?.roomId = roomID
                            }
                            msg?.let { messages.add(it) }
                        }

                        // Insert new messages into RoomDB
                        if(messages.isNotEmpty()){
                            lifecycleScope.launch(Dispatchers.IO) {
                                dao.insertMessageList(messages)
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })


//            lifecycleScope.launch(Dispatchers.IO) {
//                viewModel.loadMoreMessages(roomID!!)
//            }
            viewModel.initializeChat(roomID!!)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        messagesListener?.let { chatroomRef.removeEventListener(it) }
    }

    private fun setupScrollListener() {
        binding.rvChat.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val firstVisibleItem = layoutManager.findFirstVisibleItemPosition()
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount

                // Load more when user scrolls near the top (since layout is reversed)
                if (firstVisibleItem + visibleItemCount >= totalItemCount - 10) {
                    lifecycleScope.launch(Dispatchers.IO) {
                        viewModel.loadMoreMessages(roomID!!)
                    }
                }
            }
        })
    }
}

