package com.po4yka.dancer.ui.screens.camera

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil.compose.rememberAsyncImagePainter
import com.google.accompanist.insets.statusBarsPadding
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.po4yka.dancer.R
import com.po4yka.dancer.utils.MediaHelper.saveMediaToStorageWithTimeStamp

@androidx.camera.core.ExperimentalGetImage
@ExperimentalPermissionsApi
@Composable
fun CameraContentScreen(modifier: Modifier = Modifier) {
    var imageUri by remember { mutableStateOf(EMPTY_IMAGE_URI) }
    val context = LocalContext.current

    if (imageUri != EMPTY_IMAGE_URI) {
        Column(
            modifier =
                modifier
                    .statusBarsPadding()
                    .padding(start = 16.dp, end = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                painter = rememberAsyncImagePainter(imageUri),
                contentDescription = stringResource(id = R.string.captured_image),
            )
            Row(
                modifier =
                    Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceAround,
            ) {
                Button(
                    onClick = {
                        saveMediaToStorageWithTimeStamp(context, imageUri)
                        imageUri = EMPTY_IMAGE_URI
                    },
                ) {
                    Text(stringResource(id = R.string.save_image))
                }
                Button(
                    onClick = {
                        imageUri = EMPTY_IMAGE_URI
                    },
                ) {
                    Text(stringResource(id = R.string.remove_image))
                }
            }
        }
    } else {
        CameraScreen(
            modifier = modifier,
            onImageFile = { file ->
                imageUri = file.toUri()
            },
        )
    }
}

private val EMPTY_IMAGE_URI: Uri = Uri.parse("file://dev/null")
