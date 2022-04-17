package com.po4yka.gallerypicker.data

import androidx.paging.PagingSource
import androidx.paging.PagingState

class GalleryPickerDataSource(
    private val onFetch: (limit: Int, offset: Int) -> List<GalleryPickerImage>
) : PagingSource<Int, GalleryPickerImage>() {

    override fun getRefreshKey(state: PagingState<Int, GalleryPickerImage>): Int? {
        return state.anchorPosition?.let {
            state.closestPageToPosition(it)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(it)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, GalleryPickerImage> {
        val pageNumber = params.key ?: 0
        val pageSize = params.loadSize
        val pictures = onFetch.invoke(pageSize, pageNumber * pageSize)
        val prevKey = if (pageNumber > 0) pageNumber - 1 else null
        val nextKey = if (pictures.isNotEmpty()) pageNumber + 1 else null

        return LoadResult.Page(
            data = pictures,
            prevKey = prevKey,
            nextKey = nextKey
        )
    }

}