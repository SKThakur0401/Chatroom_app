package com.example.a20_firebase_basic_chatroom.data.dataModels

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val userId : String? = null,
    val userName : String? = null,
    val email : String? = null,
    val password : String? = null
){
    constructor() : this(null, null, null, null)
}

