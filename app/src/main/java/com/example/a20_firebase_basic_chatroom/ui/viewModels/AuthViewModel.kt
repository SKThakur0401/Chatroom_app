package com.example.a20_firebase_basic_chatroom.ui.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.a20_firebase_basic_chatroom.data.dataModels.NetworkResult
import com.example.a20_firebase_basic_chatroom.data.dataModels.User
import com.example.a20_firebase_basic_chatroom.data.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private val authRepository: AuthRepository) : ViewModel() {

    private var firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

//    private val _loginLiveData = MutableLiveData<NetworkResult<User>>()
//    val loginLiveData: LiveData<NetworkResult<User>> get() = _loginLiveData
//
//    private val _registrationLiveData = MutableLiveData<NetworkResult<User>>()
//    val registrationLiveData: LiveData<NetworkResult<User>> get() = _registrationLiveData

    val loginLiveData = authRepository.loginLiveData
    val registrationLiveData = authRepository.registrationLiveData


    fun login(email: String, password: String) {
        authRepository.login(email, password)
    }

    fun register(username: String, email: String, password: String) {
        authRepository.register(username, email, password)
    }

    fun isUserLoggedIn(): Boolean {
        return authRepository.isUserLoggedIn()
    }
}