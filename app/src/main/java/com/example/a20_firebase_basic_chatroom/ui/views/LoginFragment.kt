package com.example.a20_firebase_basic_chatroom.ui.views

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
import com.example.a20_firebase_basic_chatroom.databinding.FragmentLoginBinding
import com.example.a20_firebase_basic_chatroom.ui.viewModels.AuthViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : Fragment() {

    @Inject
    lateinit var tokenManager: TokenManager

    private lateinit var authViewModel : AuthViewModel

    private lateinit var binding : FragmentLoginBinding
    private lateinit var dbRef : DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dbRef = FirebaseDatabase.getInstance().getReference("Users")
        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        listeners()
        observers()
    }

    fun listeners(){
        binding.btnLogin.setOnClickListener{
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            if(email.isNotEmpty() && password.isNotEmpty()){
                authViewModel.login(email, password)
            } else{
                Toast.makeText(context, "Fill all Fields" , Toast.LENGTH_SHORT).show()
            }
        }

        binding.tvCreateNewAcc.setOnClickListener{
            findNavController().navigate(R.id.action_loginFragment2_to_signupFragment)
        }
    }

    fun observers(){
        authViewModel.loginLiveData.observe(viewLifecycleOwner, Observer {
            when(it){
                is NetworkResult.Success ->{
                    findNavController().navigate(R.id.action_loginFragment2_to_enterRoomID)
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
