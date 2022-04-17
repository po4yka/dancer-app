package com.po4yka.gallerypicker.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.po4yka.gallerypicker.config.GalleryPickerConfiguration
import com.po4yka.gallerypicker.data.GalleryPickerRepository
import com.po4yka.gallerypicker.ui.GalleryPickerViewModel

@Suppress("UNCHECKED_CAST")
internal class GalleryPickerViewModelFactory(
    private val galleryPickerRepository: GalleryPickerRepository,
    private val galleryPickerUriManager: GalleryPickerUriManager,
    private val galleryPickerConfiguration: GalleryPickerConfiguration,
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(GalleryPickerViewModel::class.java)) {
            GalleryPickerViewModel(
                this.galleryPickerRepository,
                this.galleryPickerConfiguration,
                this.galleryPickerUriManager
            ) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }

}