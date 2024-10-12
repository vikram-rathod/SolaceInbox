package com.devvikram.solaceinbox.model

data class Mail(
    var id: String = "",
    var senderId: String = "",
    var senderName: String = "",
    val subject: String = "",
    val recipients: ArrayList<UserModel> = ArrayList(),
    val body: String = "",
    var cDate: String = "",
)
