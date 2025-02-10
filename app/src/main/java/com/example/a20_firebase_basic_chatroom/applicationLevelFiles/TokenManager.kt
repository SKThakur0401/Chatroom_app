package com.example.a20_firebase_basic_chatroom.applicationLevelFiles

import android.content.Context
import com.example.a20_firebase_basic_chatroom.data.dataModels.User
import com.example.a20_firebase_basic_chatroom.utils.Constants.IS_USER_SIGNED_IN
import com.example.a20_firebase_basic_chatroom.utils.Constants.TOKEN_FOR_PREFS
import com.example.a20_firebase_basic_chatroom.utils.Constants.USER_IDENTITY
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.json.Json
import javax.inject.Inject

class TokenManager @Inject constructor(@ApplicationContext context : Context) {

    private val prefs = context.getSharedPreferences(TOKEN_FOR_PREFS, Context.MODE_PRIVATE)


    fun saveUser(user : User){
        val jsonString = kotlinx.serialization.json.Json.encodeToString(User.serializer(), user)
        // Above line is to convert "User" class object into a string, when getting, we'll reconvert
        // the string to the "User" class object, this is serialization and deserialization ;)

        with(prefs.edit()){
            putString(USER_IDENTITY, jsonString)
            putBoolean(IS_USER_SIGNED_IN, true)
            apply()
        }
    }

    fun getUser(): User?{
        return prefs.getString(USER_IDENTITY, null)?.let {
            Json.decodeFromString(User.serializer(), it)
        }
    }

    fun isUserSignedIn() : Boolean{
        return prefs.getBoolean(IS_USER_SIGNED_IN, false)
    }

    fun signOut(){
        with(prefs.edit()){
            putBoolean(IS_USER_SIGNED_IN, false)
            remove(USER_IDENTITY)       // Remove stored user's details
            apply()
        }
    }
}
