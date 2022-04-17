package com.po4yka.gallerypicker.data

import android.net.Uri

class GalleryPickerImage(
    val uri: Uri,
    val displayName: String?,
    internal val dateTaken: Long?,
    internal val id: Long?,
    internal val folderName: String?,
)
