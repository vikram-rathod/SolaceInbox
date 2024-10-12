package com.devvikram.solaceinbox.model

data class CategorizedEmail(
    val category: String,
    val mails: ArrayList<Mail>
)
