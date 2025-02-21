package com.example.a20_firebase_basic_chatroom.data.dataModels

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chats")
class Chats(
    @PrimaryKey
    val chatroomId: String,
    val messages : List<Message> ?= null
)