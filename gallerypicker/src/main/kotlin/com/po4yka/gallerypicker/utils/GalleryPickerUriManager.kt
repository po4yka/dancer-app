package com.po4yka.gallerypicker.utils

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.po4yka.gallerypicker.data.GalleryPickerImage

internal class GalleryPickerUriManager(private val context: Context) {

    private val photoCollection by lazy {
        if (Build.VERSION.SDK_INT > 28) {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }
    }

    private val resolver by lazy { context.contentResolver }

    val newUri = resolver.insert(photoCollection, setupPhotoDetails())

    fun getGalleryPickerImage(uri: Uri?): GalleryPickerImage? = uri?.let {
        GalleryPickerImage(
            it,
            setupPhotoDetails().getAsString(MediaStore.Images.Media.DISPLAY_NAME),
            System.currentTimeMillis(),
            null, null
        )
    }

    private fun setupPhotoDetails() = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, getFileName())
        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
    }

    private fun getFileName() = "dancer-${System.currentTimeMillis()}.jpg"

}