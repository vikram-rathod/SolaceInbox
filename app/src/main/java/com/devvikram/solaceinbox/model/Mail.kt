package com.devvikram.solaceinbox.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Mail(
    var id: String = "",
    var senderId: String = "",
    var senderName: String = "",
    var senderEmail: String = "",
    val subject: String = "",
    val recipients: ArrayList<UserModel> = ArrayList(),
    val body: String = "",
    var cDate: String = "",
    val attachments: MutableList<Attachment> = mutableListOf(),
    val type : String = ""
) : Parcelable
