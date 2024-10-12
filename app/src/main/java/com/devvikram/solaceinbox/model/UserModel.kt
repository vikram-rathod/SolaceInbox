package com.devvikram.solaceinbox.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserModel(
    var userId: String = "",
    val name: String = "",
    val email: String = "",
    val password: String = ""
) : Parcelable
