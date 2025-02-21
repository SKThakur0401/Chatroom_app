package com.example.a20_firebase_basic_chatroom.data.api

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.a20_firebase_basic_chatroom.data.dataModels.Message

@Dao
interface messagesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertChat(message: Message)

    @Query("SELECT * FROM messages WHERE roomId = :roomId ORDER BY timestamp DESC LIMIT :limit")
    fun getChatsForThisRoom(roomId: String, limit: Int) : List<Message>
}
