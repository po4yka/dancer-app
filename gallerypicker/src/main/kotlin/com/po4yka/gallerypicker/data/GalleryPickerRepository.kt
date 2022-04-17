package com.po4yka.gallerypicker.data

import android.content.Context
import androidx.paging.PagingSource
import com.po4yka.gallerypicker.utils.createCursor
import com.po4yka.gallerypicker.utils.fetchPagePicture

internal interface GalleryPickerRepository {
    suspend fun getCount(): Int
    suspend fun getByOffset(offset: Int): GalleryPickerImage?
    fun getPicturePagingSource(): PagingSource<Int, GalleryPickerImage>
}

internal class GalleryPickerRepositoryImpl(private val context: Context) : GalleryPickerRepository {

    override suspend fun getCount(): Int {
        val cursor = context.createCursor(Int.MAX_VALUE, 0) ?: return 0
        val count = cursor.count
        cursor.close()

        return count
    }

    override suspend fun getByOffset(offset: Int): GalleryPickerImage? {
        return context.fetchPagePicture(1, offset).firstOrNull()
    }

    override fun getPicturePagingSource(): PagingSource<Int, GalleryPickerImage> {
        return GalleryPickerDataSource { limit, offset -> context.fetchPagePicture(limit, offset) }
    }

}
