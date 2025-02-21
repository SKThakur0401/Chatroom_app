package com.example.a20_firebase_basic_chatroom.ui.views

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.a20_firebase_basic_chatroom.R
import com.example.a20_firebase_basic_chatroom.applicationLevelFiles.TokenManager
import com.example.a20_firebase_basic_chatroom.data.api.messagesDao
import com.example.a20_firebase_basic_chatroom.databinding.FragmentEnterRoomIDBinding
import com.example.a20_firebase_basic_chatroom.utils.Constants.ROOM_ID
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class EnterRoomID : Fragment() {

    @Inject
    lateinit var tokenManager: TokenManager

    @Inject
    lateinit var dao : messagesDao

    private lateinit var binding: FragmentEnterRoomIDBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentEnterRoomIDBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listener()
    }

    private fun listener(){

        // Just for testing!!
/*
        val roomID = "69"
        val bundle = Bundle()
        bundle.putString(ROOM_ID, roomID)
        findNavController().navigate(R.id.action_enterRoomID_to_chatFragment, bundle)
*/



        binding.btnEnterRoom.setOnClickListener {
            val roomID = binding.etRoomID.text.toString()

            if(roomID.isNotEmpty()){
                val bundle = Bundle()
                bundle.putString(ROOM_ID, roomID)
                findNavController().navigate(R.id.action_enterRoomID_to_chatFragment, bundle)
            } else{
                Toast.makeText(requireContext(), "Enter a room ID", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnSignout.setOnClickListener {
            tokenManager.signOut()

            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    dao.clearAllMessages() // Ensures database is cleared first
                }

                // Now navigate after RoomDB is cleared
                findNavController().navigate(R.id.action_enterRoomID_to_signupFragment)
            }
        }
    }

}