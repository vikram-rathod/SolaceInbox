package com.devvikram.solaceinbox.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class NavItemModel (
    val title: String = "",
    val drawable: Int = 0,
    var isSelected: Boolean = false,
    val badgeCount : Int = 0,
):Parcelable