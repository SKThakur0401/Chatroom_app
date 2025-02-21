package com.example.a20_firebase_basic_chatroom.applicationLevelFiles

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.a20_firebase_basic_chatroom.data.api.messagesDao
import com.example.a20_firebase_basic_chatroom.data.dataModels.Message

@Database(entities = [Message::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun chatsDao(): messagesDao
}
