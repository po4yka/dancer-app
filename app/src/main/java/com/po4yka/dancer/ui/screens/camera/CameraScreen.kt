package com.po4yka.dancer.ui.screens.camera

import android.Manifest
import android.content.Context
import android.os.Parcelable
import android.util.Size
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.core.UseCase
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.firebase.perf.metrics.AddTrace
import com.po4yka.dancer.R
import com.po4yka.dancer.classifier.MoveAnalyzer
import com.po4yka.dancer.classifier.PoseClassifierProcessor.Companion.IMAGE_NET_WIDTH
import com.po4yka.dancer.classifier.PoseClassifierProcessor.Companion.IMAGE_NEW_HEIGHT
import com.po4yka.dancer.models.PoseDetectionStateResult
import com.po4yka.dancer.models.RecognitionModelName
import com.po4yka.dancer.models.RecognitionModelPredictionResult
import com.po4yka.dancer.models.RecognitionState
import com.po4yka.dancer.ui.components.camera.CameraControls
import com.po4yka.dancer.ui.components.camera.CameraPreview
import com.po4yka.dancer.ui.components.persmission.Permission
import com.po4yka.dancer.ui.components.persmission.PermissionNotAvailable
import com.po4yka.dancer.ui.components.resulttable.ResultTable
import com.po4yka.dancer.utils.executor
import com.po4yka.dancer.utils.getCameraProvider
import com.po4yka.dancer.utils.switchLens
import com.po4yka.dancer.utils.switchRecognitionMode
import com.po4yka.dancer.utils.takePicture
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

@Composable
@ExperimentalPermissionsApi
@androidx.camera.core.ExperimentalGetImage
@AddTrace(name = "camera_screen", enabled = true)
fun CameraScreen(
    modifier: Modifier = Modifier,
    onImageFile: (File) -> Unit = { },
) {
    val context = LocalContext.current
    val executor = ContextCompat.getMainExecutor(context)

    Permission(
        permission = Manifest.permission.CAMERA,
        rationaleTitle = stringResource(id = R.string.recognize_from_camera),
        rationaleIconId = R.drawable.ic_camera_light,
        rationaleDescription = stringResource(id = R.string.camera_permission_request_text),
        permissionNotAvailableContent = {
            PermissionNotAvailable(
                unavailableExplanationResId = R.string.can_not_work_with_no_camera
            )
        }
    ) {
        Box {
            val lifecycleOwner = LocalLifecycleOwner.current
            val coroutineScope = rememberCoroutineScope()
            var previewUseCase by remember {
                mutableStateOf<UseCase>(
                    Preview.Builder().build()
                )
            }
            var cameraLens by remember { mutableStateOf(CameraSelector.LENS_FACING_BACK) }

            var recognitionState by remember {
                mutableStateOf(RecognitionState.ACTIVE)
            }
            var recognitionSuccess by remember {
                mutableStateOf(PoseDetectionStateResult.NOT_DETECTED)
            }

            val movesProbabilities = rememberMutableStateListOf<RecognitionModelPredictionResult>()

            val imageCaptureUseCase: ImageCapture by remember {
                mutableStateOf(
                    ImageCapture
                        .Builder()
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                        .build()
                )
            }

            val imageAnalysisUseCase: ImageAnalysis by remember {
                mutableStateOf(
                    ImageAnalysis
                        .Builder()
                        .setTargetResolution(Size(IMAGE_NET_WIDTH, IMAGE_NEW_HEIGHT))
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                        .build()
                )
            }

            val analyzer = remember {
                MoveAnalyzer(context) { analyzeResults ->
                    val newProbabilities = analyzeResults.results
                    recognitionSuccess =
                        if (analyzeResults.isDetected) {
                            PoseDetectionStateResult.DETECTED
                        } else {
                            PoseDetectionStateResult.NOT_DETECTED
                        }
                    movesProbabilities.apply {
                        clear()
                        newProbabilities.forEach { add(it) }
                    }
                }
            }
            imageAnalysisUseCase.setAnalyzer(executor) { imageProxy: ImageProxy ->
                try {
                    analyzer.analyze(imageProxy)
                } finally {
                    imageProxy.close()
                }
            }

            Box {
                CameraPreview(
                    modifier = Modifier.fillMaxSize(),
                    onUseCase = {
                        previewUseCase = it
                    }
                )
                if (recognitionState == RecognitionState.ACTIVE) {
                    if (!analyzer.isActive) analyzer.start()
                    if (movesProbabilities.size == RecognitionModelName.values().size) {
                        ResultTable(
                            modifier = Modifier.align(Alignment.TopCenter),
                            isDetected = recognitionSuccess,
                            recognitionModelPredictionResults = movesProbabilities
                        )
                    }
                } else {
                    analyzer.stop()
                }
                CameraControls(
                    modifier = modifier
                        .align(Alignment.BottomCenter),
                    recognitionMode = recognitionState,
                    onCaptureClicked = {
                        coroutineScope.launch(Dispatchers.IO) {
                            onImageFile(imageCaptureUseCase.takePicture(context.executor))
                        }
                    },
                    onLensChangeClicked = {
                        cameraLens = switchLens(cameraLens)
                    },
                    onRecognitionModeSwitchClicked = {
                        recognitionState = switchRecognitionMode(recognitionState)
                    }
                )
            }

            LaunchedEffect(previewUseCase, cameraLens) {
                launchedEffects(
                    context,
                    lifecycleOwner,
                    previewUseCase,
                    imageCaptureUseCase,
                    imageAnalysisUseCase,
                    cameraLens
                )
            }

            DisposableEffect(lifecycleOwner) {
                onDispose {
                    analyzer.stop()
                }
            }
        }
    }
}

@Composable
fun <T : Parcelable> rememberMutableStateListOf(vararg elements: T): SnapshotStateList<T> {
    return rememberSaveable(
        saver = listSaver(
            save = { it.toList() },
            restore = { it.toMutableStateList() }
        )
    ) {
        elements.toList().toMutableStateList()
    }
}

private suspend fun launchedEffects(
    context: Context,
    lifecycleOwner: LifecycleOwner,
    previewUseCase: UseCase,
    imageCaptureUseCase: ImageCapture,
    imageAnalysisUseCase: ImageAnalysis,
    cameraLens: Int
) {
    val cameraProvider = context.getCameraProvider()
    val cameraSelector = CameraSelector
        .Builder()
        .requireLensFacing(cameraLens)
        .build()
    val printFailedCamera: (ex: Exception) -> Unit = { ex: Exception ->
        Timber.e(ex, "Failed to bind camera use cases")
    }

    try {
        // Must unbind the use-cases before rebinding them.
        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(
            lifecycleOwner,
            cameraSelector,
            previewUseCase,
            imageCaptureUseCase
        )
        cameraProvider.bindToLifecycle(
            lifecycleOwner,
            cameraSelector,
            imageAnalysisUseCase
        )
    } catch (ex: IllegalStateException) {
        printFailedCamera(ex)
    } catch (ex: IllegalArgumentException) {
        printFailedCamera(ex)
    }
}

@androidx.camera.core.ExperimentalGetImage
@ExperimentalPermissionsApi
@Composable
fun CameraScreenPreview() {
    Scaffold(
        modifier = Modifier
            .size(125.dp)
            .wrapContentSize()
    ) { contentPadding ->
        CameraScreen(modifier = Modifier.padding(contentPadding))
    }
}
