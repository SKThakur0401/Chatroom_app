package com.example.a20_firebase_basic_chatroom.ui.views

import android.annotation.SuppressLint
import android.graphics.Rect
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.a20_firebase_basic_chatroom.R
import com.example.a20_firebase_basic_chatroom.applicationLevelFiles.TokenManager
import com.example.a20_firebase_basic_chatroom.data.dataModels.Message
import com.example.a20_firebase_basic_chatroom.data.dataModels.User
import com.example.a20_firebase_basic_chatroom.databinding.FragmentChatBinding
import com.example.a20_firebase_basic_chatroom.utils.Constants.ROOM_ID
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ChatFragment : Fragment() {

    private lateinit var binding : FragmentChatBinding
    private lateinit var chatroomRef : DatabaseReference
    private var roomID :String? = null
    lateinit var user : User            // Here "user" will be the current user, the person who is
                                // using this account, in this object we'll store user's name, id, etc..

    @Inject
    lateinit var tokenManager: TokenManager

    private lateinit var adapter : ChatAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }


    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        user = tokenManager.getUser()!!
        roomID = arguments?.getString(ROOM_ID)
        roomID?.let {roomID ->
            chatroomRef = FirebaseDatabase.getInstance().getReference("Chats").child(roomID)
        }

        adapter = tokenManager.getUser()!!.userId?.let { ChatAdapter(it) }!!
        binding.rvChat.adapter = adapter
        binding.rvChat.layoutManager = LinearLayoutManager(requireContext()).apply {
            stackFromEnd = true
        }

        binding.includeTopBar.tvRoomId.text = "Room- ${roomID ?: "Unknown Room"}"
        listener()

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                binding.rvChat.post {
                    binding.rvChat.scrollToPosition(adapter.itemCount - 1)
                }
                isEnabled = false
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        })
    }

    private fun listener(){
        chatroomRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val message = snapshot.children.map { it.getValue(Message::class.java) }

                if (message.isNotEmpty()) {
                    val oldLastMessagePosition = adapter.itemCount - 1

                    adapter.submitList(message) {
                        binding.rvChat.scrollToPosition(adapter.itemCount - 1)

                        // Ensure previous last message updates
                        if (oldLastMessagePosition >= 0) {
                            adapter.notifyItemChanged(oldLastMessagePosition)
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })

        binding.includeTextSender.btnSendMsg.setOnClickListener {
            val text = binding.includeTextSender.etTypeMessage.text.toString()      // here "text" will have the text msg

            val msgId = chatroomRef.push().key          // Each message should have a unique id,
                                                // This will generate that unique id

            msgId?.let {messageId ->
                val msg = user.userId?.let { senderId -> user.userName?.let { senderName ->
                    Message(msgId, text, senderId,
                        senderName
                    )
                } }
                chatroomRef.child(messageId).setValue(msg)
            } ?: run {
                Toast.makeText(requireContext(), "Error sending message", Toast.LENGTH_SHORT).show()
            }

            binding.includeTextSender.etTypeMessage.text.clear()
        }

    }

}

