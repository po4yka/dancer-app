package com.po4yka.dancer.ui.screens

import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.core.UseCase
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.po4yka.dancer.R
import com.po4yka.dancer.ui.components.Permission
import com.po4yka.dancer.ui.components.PermissionNotAvailable
import com.po4yka.dancer.ui.components.camera.CameraControls
import com.po4yka.dancer.ui.components.camera.CameraPreview
import com.po4yka.dancer.utils.executor
import com.po4yka.dancer.utils.getCameraProvider
import com.po4yka.dancer.utils.switchLens
import com.po4yka.dancer.utils.takePicture
import java.io.File
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import timber.log.Timber

@ExperimentalPermissionsApi
@ExperimentalCoroutinesApi
@Composable
fun CameraScreen(
    modifier: Modifier = Modifier,
    onImageFile: (File) -> Unit = { },
) {
    val context = LocalContext.current
    Permission(
        permission = android.Manifest.permission.CAMERA,
        rationaleTitle = stringResource(id = R.string.recognize_from_camera),
        rationaleIconId = R.drawable.ic_camera_light,
        rationaleDescription = stringResource(id = R.string.camera_permission_request_text),
        permissionNotAvailableContent = {
            PermissionNotAvailable()
        }
    ) {
        Box {
            val lifecycleOwner = LocalLifecycleOwner.current
            val coroutineScope = rememberCoroutineScope()
            var previewUseCase by remember { mutableStateOf<UseCase>(Preview.Builder().build()) }
            var cameraLens by remember { mutableStateOf(CameraSelector.LENS_FACING_BACK) }
            val imageCaptureUseCase by remember {
                mutableStateOf(
                    ImageCapture.Builder()
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
                        .build()
                )
            }
            Box {
                CameraPreview(
                    modifier = Modifier.fillMaxSize(),
                    onUseCase = {
                        previewUseCase = it
                    }
                )
                CameraControls(
                    modifier = modifier
                        .align(Alignment.BottomCenter),
                    onCaptureClicked = {
                        coroutineScope.launch {
                            onImageFile(imageCaptureUseCase.takePicture(context.executor))
                        }
                    },
                    onLensChangeClicked = {
                        cameraLens = switchLens(cameraLens)
                    },
                    onRecognitionModeSwitchClicked = {} // TODO: pass correct value
                )
            }
            LaunchedEffect(previewUseCase, cameraLens) {
                val cameraProvider = context.getCameraProvider()
                val cameraSelector = CameraSelector.Builder().requireLensFacing(cameraLens).build()
                val printFailedCamera: (ex: Exception) -> Unit = { ex: Exception ->
                    Timber.e(ex, "Failed to bind camera use cases")
                }
                try {
                    // Must unbind the use-cases before rebinding them.
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner, cameraSelector, previewUseCase, imageCaptureUseCase
                    )
                } catch (ex: IllegalStateException) {
                    printFailedCamera(ex)
                } catch (ex: IllegalArgumentException) {
                    printFailedCamera(ex)
                }
            }
        }
    }
}

@ExperimentalPermissionsApi
@ExperimentalCoroutinesApi
@androidx.compose.ui.tooling.preview.Preview
@Composable
fun CameraScreenPreview() {
    Scaffold(
        modifier = Modifier
            .size(125.dp)
            .wrapContentSize()
    ) { CameraScreen() }
}
