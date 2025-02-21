package com.example.a20_firebase_basic_chatroom.data.dataModels

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "messages")
data class Message(
    @PrimaryKey var msgId : String = "",
    var message : String? = null,
    var senderId : String? = null,
    var senderName: String? = null,
    var roomId: String?= null,
    var timestamp: Long = System.currentTimeMillis()
){
    constructor() : this("", null, null, null, null, System.currentTimeMillis())
}
