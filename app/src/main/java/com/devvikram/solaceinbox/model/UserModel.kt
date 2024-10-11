package com.devvikram.solaceinbox.model

data class UserModel(
    var userId: String = "",
    val name: String = "",
    val email: String = "",
    val password: String = ""
)
