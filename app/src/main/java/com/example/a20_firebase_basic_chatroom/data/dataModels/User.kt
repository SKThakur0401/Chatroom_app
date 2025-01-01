package com.example.a20_firebase_basic_chatroom.data.dataModels

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val userId : String,
    val userName : String,
    val email : String,
    val password : String
)

