package com.example.a20_firebase_basic_chatroom.ui.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.a20_firebase_basic_chatroom.applicationLevelFiles.TokenManager
import com.example.a20_firebase_basic_chatroom.data.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private val authRepository: AuthRepository) : ViewModel() {

    private var firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    val loginLiveData = authRepository.loginLiveData
    val registrationLiveData = authRepository.registrationLiveData

    @Inject
    lateinit var tokenManager: TokenManager


    fun login(email: String, password: String) {
        authRepository.login(email, password)
    }

    fun register(username: String, email: String, password: String) {
        authRepository.register(username, email, password)
    }

    fun isUserLoggedIn(): Boolean {
        return authRepository.isUserLoggedIn()
    }

    fun signOut(){
        tokenManager.signOut()
        authRepository.signoutUser()
    }
}