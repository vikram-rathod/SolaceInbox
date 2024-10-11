package com.devvikram.solaceinbox.model

data class NavItemModel (
    val title: String = "",
    val drawable: Int = 0,
    var isSelected: Boolean = false,
    val badgeCount : Int = 0,
)