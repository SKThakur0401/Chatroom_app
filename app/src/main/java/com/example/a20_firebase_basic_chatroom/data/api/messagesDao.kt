package com.example.a20_firebase_basic_chatroom.data.api

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.a20_firebase_basic_chatroom.data.dataModels.Message

@Dao
interface messagesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMessage(message: Message)

    @Query("SELECT * FROM messages WHERE roomId = :roomId ORDER BY timestamp ASC")
    fun getChatsForThisRoom(roomId: String) : LiveData<List<Message>>

    @Query("SELECT COUNT(*) FROM messages WHERE roomId = :roomId")
    fun hasMessages(roomId: String): Long

    @Query("SELECT MAX(timestamp) FROM messages WHERE roomId = :roomId")
    fun getLatestMessageTimestamp(roomId: String): Long?

    @Query("DELETE FROM messages")
    fun clearAllMessages()
}
