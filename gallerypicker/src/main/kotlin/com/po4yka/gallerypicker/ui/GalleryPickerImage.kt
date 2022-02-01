package com.po4yka.gallerypicker.ui

import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.transform.RoundedCornersTransformation
import com.po4yka.gallerypicker.config.GalleryPickerConfiguration
import com.po4yka.gallerypicker.data.GalleryPickerImage
import com.po4yka.gallerypicker.theme.GalleryPickerDimens
import kotlinx.coroutines.flow.StateFlow

@Composable
internal fun GalleryPickerImage(
    modifier: Modifier,
    galleryPickerImage: GalleryPickerImage,
    selectedImages: StateFlow<List<GalleryPickerImage>>,
    galleryPickerConfiguration: GalleryPickerConfiguration,
    onSelectedPhoto: (GalleryPickerImage, isSelected: Boolean) -> Unit,
) {
    val selected = remember { mutableStateOf(false) }
    val images by selectedImages.collectAsState(initial = emptyList())
    val transition = updateTransition(selected.value, label = "change-padding")

    val animatedPadding by transition.animateDp(label = "change-padding") { isSelected ->
        if (isSelected) {
            GalleryPickerDimens.One
        } else {
            GalleryPickerDimens.Zero
        }

    }

    Box(
        modifier = modifier.size(GalleryPickerDimens.Sixteen),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = rememberAsyncImagePainter(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(galleryPickerImage.uri)
                    .crossfade(true)
                    .transformations(
                        RoundedCornersTransformation(
                            topLeft = GalleryPickerDimens.cornerRadius,
                            bottomLeft = GalleryPickerDimens.cornerRadius,
                            bottomRight = GalleryPickerDimens.cornerRadius,
                            topRight = GalleryPickerDimens.cornerRadius
                        )
                    )
                    .build(),
            ),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.padding(animatedPadding)
        )
        Box(
            modifier = Modifier
                .clickable {
                    if (!galleryPickerConfiguration.multipleImagesAllowed) {
                        if (images.count() < 1) {
                            selected.value = !selected.value
                            onSelectedPhoto(galleryPickerImage, selected.value)
                        } else {
                            selected.value = false
                            onSelectedPhoto(galleryPickerImage, selected.value)
                        }
                    } else {
                        selected.value = !selected.value
                        onSelectedPhoto(galleryPickerImage, selected.value)
                    }
                }
                .size(GalleryPickerDimens.Sixteen),
            contentAlignment = Alignment.TopEnd,
        ) {
            GalleryPickerImageIndicator(
                text = images.indexOf(galleryPickerImage).plus(1).toString()
            )
        }
    }
}
