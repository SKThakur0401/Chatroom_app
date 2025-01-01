package com.example.a20_firebase_basic_chatroom.data.dataModels

import kotlinx.serialization.Serializable

@Serializable
data class Message(
    val senderId : String = "",
    val senderName: String = "",
    val message: String = "",
    val msgId : String = "",
    val timestamp: Long = System.currentTimeMillis()
)
