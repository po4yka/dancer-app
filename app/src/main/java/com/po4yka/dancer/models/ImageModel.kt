package com.po4yka.dancer.models

import android.os.Parcelable
import java.util.*
import kotlinx.parcelize.Parcelize

@Parcelize
data class ImageModel(
    val id: String = UUID.randomUUID().toString(),
) : Parcelable {
    companion object Keys {
        const val IMG = "image"
    }
}
