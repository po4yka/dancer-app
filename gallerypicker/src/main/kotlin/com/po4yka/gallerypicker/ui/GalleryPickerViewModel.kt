package com.po4yka.gallerypicker.ui

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.po4yka.gallerypicker.config.GalleryPickerConfiguration
import com.po4yka.gallerypicker.data.GalleryPickerImage
import com.po4yka.gallerypicker.data.GalleryPickerRepository
import com.po4yka.gallerypicker.utils.GalleryPickerUriManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

internal class GalleryPickerViewModel(
    private val galleryPickerRepository: GalleryPickerRepository,
    private val galleryPickerConfiguration: GalleryPickerConfiguration,
    private val galleryPickerUriManager: GalleryPickerUriManager,
) : ViewModel() {

    private val selectedImageList: MutableList<GalleryPickerImage> = ArrayList()
    private val _selectedImage = MutableStateFlow(emptyList<GalleryPickerImage>())
    private var uri: Uri? = null

    val selectedImage: StateFlow<List<GalleryPickerImage>> = _selectedImage

    fun getGalleryPickImage() = galleryPickerUriManager.getGalleryPickerImage(uri)

    fun getImages(): Flow<PagingData<GalleryPickerImage>> = Pager(
        config = PagingConfig(
            pageSize = PAGE_SIZE,
            initialLoadSize = INITIAL_LOAD_SIZE,
            enablePlaceholders = true
        )
    ) {
        galleryPickerRepository.getPicturePagingSource()
    }.flow.cachedIn(viewModelScope)

    fun isPhotoSelected(img: GalleryPickerImage, isSelected: Boolean) {
        if (isSelected) {
            if (galleryPickerConfiguration.multipleImagesAllowed) {
                selectedImageList.add(img)
            } else {
                if (selectedImageList.isEmpty() && selectedImageList.count() < 1) {
                    selectedImageList.add(img)
                }
            }
        } else {
            selectedImageList.filter { it.id == img.id }
                .forEach { selectedImageList.remove(it) }
        }
        _selectedImage.value = (selectedImageList).toSet().toList()
    }

    fun getCameraImageUri(): Uri? {
        uri = galleryPickerUriManager.newUri
        return uri
    }

    private companion object {
        const val PAGE_SIZE = 50
        const val INITIAL_LOAD_SIZE = 50
    }

}