package com.devvikram.solaceinbox.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Attachment(
    var id: String = "",
    var fileName: String = "",
    var filePath: String = "",
    var fileSize: Long = 0,
    var fileType: String = "",
    var downloadUrl: String = ""
) : Parcelable

