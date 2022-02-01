package com.po4yka.gallerypicker.utils

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.os.Build
import android.provider.MediaStore
import androidx.core.os.bundleOf
import com.po4yka.gallerypicker.data.GalleryPickerImage
import com.po4yka.gallerypicker.utils.ImageCheck.MIN_IMAGE_HEIGHT
import com.po4yka.gallerypicker.utils.ImageCheck.MIN_IMAGE_WIDTH

private val projection = arrayOf(
    MediaStore.Images.Media._ID,
    MediaStore.Images.Media.DISPLAY_NAME,
    MediaStore.Images.Media.DATE_TAKEN,
    MediaStore.Images.Media.BUCKET_DISPLAY_NAME
)

private object ImageCheck {
    const val MIN_IMAGE_WIDTH = 180.0f
    const val MIN_IMAGE_HEIGHT = 270.0f
}

internal fun Context.createCursor(limit: Int, offset: Int): Cursor? {
    val selection =
        "${MediaStore.MediaColumns.WIDTH} >= $MIN_IMAGE_WIDTH AND ${MediaStore.MediaColumns.HEIGHT} >= $MIN_IMAGE_HEIGHT"
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val bundle = bundleOf(
            ContentResolver.QUERY_ARG_OFFSET to offset,
            ContentResolver.QUERY_ARG_LIMIT to limit,
            ContentResolver.QUERY_ARG_SQL_SELECTION to selection,
            ContentResolver.QUERY_ARG_SORT_COLUMNS to arrayOf(MediaStore.Images.Media.DATE_ADDED),
            ContentResolver.QUERY_ARG_SORT_DIRECTION to ContentResolver.QUERY_SORT_DIRECTION_DESCENDING
        )
        contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            bundle,
            null
        )
    } else {
        contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            null,
            "${MediaStore.Images.Media.DATE_ADDED} DESC LIMIT $limit OFFSET $offset",
            null
        )
    }
}

internal fun Context.fetchPagePicture(limit: Int, offset: Int): List<GalleryPickerImage> {

    val pictures = ArrayList<GalleryPickerImage>()
    val cursor = createCursor(limit, offset)

    if (cursor?.moveToFirst() == true) {
        cursor.use {
            val idColumn = it.getColumnIndex(projection[0])
            val displayNameColumn = it.getColumnIndex(projection[1])
            val dateTakenColumn = it.getColumnIndex(projection[2])
            val bucketDisplayName = it.getColumnIndex(projection[3])

            if (idColumn == -1 || displayNameColumn == -1 || dateTakenColumn == -1 || bucketDisplayName == -1) return@use

            do {
                val id = it.getLong(idColumn)
                val contentUri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    id
                )

                val dateTaken = it.getLong(dateTakenColumn)
                val displayName = it.getString(displayNameColumn)
                val folderName = it.getString(bucketDisplayName)

                pictures.add(
                    GalleryPickerImage(
                        contentUri,
                        displayName,
                        dateTaken,
                        id,
                        folderName.toString()
                    )
                )

            } while (it.moveToNext())
        }
        cursor.close()
        return pictures
    } else {
        return emptyList()
    }
}
