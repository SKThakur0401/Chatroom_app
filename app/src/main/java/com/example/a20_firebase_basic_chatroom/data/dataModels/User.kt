package com.example.a20_firebase_basic_chatroom.data.dataModels

import com.example.a20_firebase_basic_chatroom.utils.Constants
import com.example.a20_firebase_basic_chatroom.utils.Constants.C
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val userId : String? = null,
    val userName : String? = null,
    val email : String? = null,
    val password : String? = null,
    val playerRank : Int = C        // In constants file it is "C" rank
){
    constructor() : this(null, null, null, null)
}
