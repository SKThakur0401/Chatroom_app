package com.example.a20_firebase_basic_chatroom.applicationLevelFiles

import android.content.Context
import com.example.a20_firebase_basic_chatroom.data.dataModels.User
import com.example.a20_firebase_basic_chatroom.utils.Constants.IS_USER_SIGNED_IN
import com.example.a20_firebase_basic_chatroom.utils.Constants.TOKEN_FOR_PREFS
import com.example.a20_firebase_basic_chatroom.utils.Constants.USER_IDENTITY
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class TokenManager @Inject constructor(@ApplicationContext context : Context) {

    private val prefs = context.getSharedPreferences(TOKEN_FOR_PREFS, Context.MODE_PRIVATE)


    fun saveUser(user : User){
        val jsonString = kotlinx.serialization.json.Json.encodeToString(User.serializer(), user)
        // Above line is to convert "User" class object into a string, when getting, we'll reconvert
        // the string to the "User" class object, this is serialization and deserialization ;)

        val editor = prefs.edit()
        editor.putString(USER_IDENTITY, jsonString)
        editor.putBoolean(IS_USER_SIGNED_IN, true)
        editor.apply()
    }

    fun getUser(): User?{
        return prefs.getString(USER_IDENTITY, null)?.let {
            kotlinx.serialization.json.Json.decodeFromString(User.serializer(), it)
        }
    }

    fun isUserSignedIn() : Boolean{
        return prefs.getBoolean(IS_USER_SIGNED_IN, false)
    }

    fun signOut(){
        val editor = prefs.edit()
        editor.putBoolean(IS_USER_SIGNED_IN, false)
        editor.apply()
    }
}