package com.example.a20_firebase_basic_chatroom.di

import android.content.Context
import androidx.room.Room
import com.example.a20_firebase_basic_chatroom.applicationLevelFiles.AppDatabase
import com.example.a20_firebase_basic_chatroom.data.api.messagesDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object RoomModule {

    @Provides
    @Singleton
    fun getRoomDB(@ApplicationContext appContext:Context):AppDatabase{
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            "app_database"
        ).build()
    }

    @Provides
    @Singleton
    fun getMessagesDao(roomDb: AppDatabase): messagesDao{
        return roomDb.chatsDao()
    }
}
