package com.example.a20_firebase_basic_chatroom.ui.views

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.a20_firebase_basic_chatroom.R
import com.example.a20_firebase_basic_chatroom.applicationLevelFiles.TokenManager
import com.example.a20_firebase_basic_chatroom.data.dataModels.NetworkResult
import com.example.a20_firebase_basic_chatroom.data.dataModels.User
import com.example.a20_firebase_basic_chatroom.databinding.FragmentSignupBinding
import com.example.a20_firebase_basic_chatroom.ui.viewModels.AuthViewModel
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SignupFragment : Fragment() {

    @Inject
    lateinit var tokenManager: TokenManager

    lateinit var authViewModel: AuthViewModel

    private lateinit var binding : FragmentSignupBinding
    private lateinit var dbRef : DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(tokenManager.isUserSignedIn()){
            findNavController().navigate(R.id.action_signupFragment_to_enterRoomID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSignupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dbRef = FirebaseDatabase.getInstance().getReference("Users")
        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        listener()
        observer()
    }

    private fun listener(){
        binding.btnSignup.setOnClickListener {
            val userName = binding.etUsername.text.toString()
            val email = binding.etRegEmail.text.toString()
            val password = binding.etRegPassword.text.toString()

            if(userName.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()){
                authViewModel.register(userName, email, password)
            } else{
                Toast.makeText(context, "Fill all Fields" , Toast.LENGTH_SHORT).show()
            }
        }

        binding.tvAlreadyHaveAcc.setOnClickListener {
            findNavController().navigate(R.id.action_signupFragment_to_loginFragment2)
        }
    }

    private fun observer()
    {
        authViewModel.registrationLiveData.observe(viewLifecycleOwner, Observer{
            when(it){
                is NetworkResult.Success ->{
                    findNavController().navigate(R.id.action_signupFragment_to_enterRoomID)
                }

                is NetworkResult.Error ->{
                    Toast.makeText(context, it.message.toString(), Toast.LENGTH_SHORT).show()
                }

                is NetworkResult.Loading ->{

                }
            }
        })
    }
}