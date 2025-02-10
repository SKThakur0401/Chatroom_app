package com.example.a20_firebase_basic_chatroom.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.a20_firebase_basic_chatroom.applicationLevelFiles.TokenManager
import com.example.a20_firebase_basic_chatroom.data.dataModels.NetworkResult
import com.example.a20_firebase_basic_chatroom.data.dataModels.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import javax.inject.Inject

class AuthRepository @Inject constructor() {

    val firebaseAuth = FirebaseAuth.getInstance()
    val firebaseDb = FirebaseDatabase.getInstance()

    @Inject
    lateinit var tokenManager : TokenManager


    private val _loginLiveData = MutableLiveData<NetworkResult<User>>()
    val loginLiveData: LiveData<NetworkResult<User>> get() = _loginLiveData
    private val _registrationLiveData = MutableLiveData<NetworkResult<User>>()
    val registrationLiveData: LiveData<NetworkResult<User>> get() = _registrationLiveData


    val _userStateLiveData = MutableLiveData<NetworkResult<User>>()
//    val userStateLiveData : LiveData<NetworkResult<FirebaseUser>> get() = _userStateLiveData

//    private val _userApiResponseLiveData = MutableLiveData<Event<NetworkResult<UserAndToken>>>()

    fun login(email: String, password: String):LiveData<NetworkResult<User>> {
        _loginLiveData.value = NetworkResult.Loading()

        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task->
                if(task.isSuccessful){
                    val firebaseUser = task.result.user!!

                    val dbRef = firebaseDb.getReference("Users").child(firebaseUser.uid)
                    dbRef.get().addOnSuccessListener { snapshot ->
                        if (snapshot.exists()) {
                            val user = snapshot.getValue(User::class.java)!!
                            tokenManager.saveUser(user)
                            _loginLiveData.value = NetworkResult.Success(user)

                        } else {
                            println("User not found!")
                        }
                    }.addOnFailureListener {
                        println("Failed to fetch user data: ${it.message}")
                    }
                } else{
                    _loginLiveData.value = NetworkResult.Error(msg = task.exception?.message.toString())
                }
            }
            .addOnFailureListener { task->
                _loginLiveData.value = NetworkResult.Error(msg = task.message.toString())
            }

        return _loginLiveData
    }


    // So for registration what we are doing is : Using "FirebaseAuth" to create an "User" using "email" and "password"
    // After that user object is created using func "createUserWithEmailAndPassword" we obtain the "user object" using
    // firebaseAuthResponse.result.user!!   now this is "FirebaseUser" class object, it contains "email" and "uid",
    // this "uid" will be unique identification for each user,  NOTE: U CAN'T OBTAIN PASSWORD AS IT IS ENCRYPTED FOR SECURITY PURPOSE
    // so in our Realtime DB we are also storing things other than "email" and "uid" right, so we'll create a "users" node in our
    // realtime db .. which will store the various other things related to the user... like user's name, email, profilePic, etc...

    fun register(username: String, email: String, password: String): LiveData<NetworkResult<User>> {
        _registrationLiveData.value = NetworkResult.Loading()

        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener{
                if(it.isSuccessful){
                    val firebaseUser = it.result.user!!         // We recieve object of "FirebaseUser" class

                    val user = User(firebaseUser.uid, username, email, password)        // This is our "User" class object, we'll store it in our DB, it will contain extra details like profilePic, role-of-user, etc....
                    firebaseDb.getReference("Users").child(firebaseUser.uid).setValue(user)
                        .addOnSuccessListener {
                            tokenManager.saveUser(user)
                            _registrationLiveData.value = NetworkResult.Success(user)
                        }
                        .addOnFailureListener {failureResponse->
                            _registrationLiveData.value = NetworkResult.Error(msg = failureResponse.message.toString())
                        }

                } else{
                    _registrationLiveData.value = NetworkResult.Error(msg = it.exception?.message.toString())
                }
            }
            .addOnFailureListener{
                _registrationLiveData.value = NetworkResult.Error(msg = it.message.toString())
            }

        return _registrationLiveData
    }

    fun isUserLoggedIn(): Boolean {
        return firebaseAuth.currentUser != null
    }

    fun signoutUser() {
        firebaseAuth.signOut()
    }
}