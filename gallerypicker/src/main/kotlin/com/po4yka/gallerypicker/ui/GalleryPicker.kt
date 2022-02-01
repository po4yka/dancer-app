package com.po4yka.gallerypicker.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.po4yka.gallerypicker.config.GalleryPickerConfiguration
import com.po4yka.gallerypicker.data.GalleryPickerImage
import com.po4yka.gallerypicker.data.GalleryPickerRepositoryImpl
import com.po4yka.gallerypicker.theme.GalleryPickerDimens
import com.po4yka.gallerypicker.utils.GalleryPickerUriManager
import com.po4yka.gallerypicker.utils.GalleryPickerViewModelFactory

@ExperimentalFoundationApi
@Composable
fun GalleryPicker(
    modifier: Modifier = Modifier,
    galleryPickerConfiguration: GalleryPickerConfiguration = GalleryPickerConfiguration()
) {

    val context = LocalContext.current
    val galleryPickerViewModel: GalleryPickerViewModel = viewModel(
        factory = GalleryPickerViewModelFactory(
            GalleryPickerRepositoryImpl(
                context,
            ),
            GalleryPickerUriManager(context),
            galleryPickerConfiguration
        )
    )

    val lazyGalleryPickerImages: LazyPagingItems<GalleryPickerImage> =
        galleryPickerViewModel.getImages().collectAsLazyPagingItems()

    Scaffold(
        modifier = modifier.fillMaxWidth()
    ) { contentPadding ->
        val contentModifier = Modifier.padding(2.dp)
        val imagesCount = lazyGalleryPickerImages.itemCount

        if (imagesCount > 0) {
            LazyVerticalGrid(
                modifier = Modifier
                    .background(MaterialTheme.colors.surface)
                    .padding(contentPadding),
                columns = GridCells.Fixed(3)
            ) {

                items(imagesCount) { index ->
                    lazyGalleryPickerImages[index]?.let { galleryPickImage ->
                        GalleryPickerImage(
                            modifier = contentModifier,
                            galleryPickerImage = galleryPickImage,
                            galleryPickerConfiguration = galleryPickerConfiguration,
                            selectedImages = galleryPickerViewModel.selectedImage,
                            onSelectedPhoto = { image, isSelected ->
                                galleryPickerViewModel.isPhotoSelected(
                                    img = image,
                                    isSelected = isSelected
                                )
                            }
                        )
                    }
                }

            }
        } else {
            NoImagesPlaceholder()
        }
    }

}

@Composable
internal fun GalleryPickerImageIndicator(text: String) {
    if (text.toInt() > 0) {
        val backgroundColor = MaterialTheme.colors.primary
        val textColor = MaterialTheme.colors.onPrimary

        Text(
            text = text,
            textAlign = TextAlign.Center,
            color = textColor,
            fontStyle = FontStyle.Normal,
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.sp,
            modifier = Modifier
                .drawBehind {
                    drawCircle(backgroundColor)
                }
                .padding(GalleryPickerDimens.One)
        )
    }
}

@Preview("default")
@ExperimentalFoundationApi
@Composable
fun GalleryPickerPreview() {
    GalleryPicker()
}
