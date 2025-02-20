package com.example.a20_firebase_basic_chatroom.data.dataModels

import androidx.room.Entity
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "messages")
data class Message(
    val msgId : String? = null,
    val message : String? = null,
    val senderId : String? = null,
    val senderName: String? = null,
    val roomId: String?= null,
    val timestamp: Long = System.currentTimeMillis()
){
    constructor() : this(null, null, null, null, null, System.currentTimeMillis())
}
