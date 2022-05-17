package com.po4yka.dancer.ui.screens.camera

import android.Manifest
import android.content.Context
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
import androidx.compose.runtime.collectAsState
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
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.LifecycleOwner
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.firebase.perf.metrics.AddTrace
import com.po4yka.dancer.R
import com.po4yka.dancer.domain.model.PosePrediction
import com.po4yka.dancer.models.PoseDetectionStateResult
import com.po4yka.dancer.models.RecognitionModelHelper
import com.po4yka.dancer.models.RecognitionModelName
import com.po4yka.dancer.models.RecognitionModelPredictionResult
import com.po4yka.dancer.models.RecognitionState
import com.po4yka.dancer.ui.components.camera.CameraControls
import com.po4yka.dancer.ui.components.camera.CameraPreview
import com.po4yka.dancer.ui.components.persmission.Permission
import com.po4yka.dancer.ui.components.persmission.PermissionNotAvailable
import com.po4yka.dancer.ui.components.resulttable.ResultTable
import com.po4yka.dancer.ui.viewmodel.CameraUiState
import com.po4yka.dancer.ui.viewmodel.CameraViewModel
import com.po4yka.dancer.utils.executor
import com.po4yka.dancer.utils.getCameraProvider
import com.po4yka.dancer.utils.switchLens
import com.po4yka.dancer.utils.switchRecognitionMode
import com.po4yka.dancer.utils.takePicture
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File

/**
 * Converts domain PosePrediction to UI RecognitionModelPredictionResult.
 */
private fun PosePrediction.toUiModel(): RecognitionModelPredictionResult {
    val recognitionName = RecognitionModelHelper.getClassById(this.moveName)
    return RecognitionModelPredictionResult(
        name = recognitionName,
        probability = this.probability,
    )
}

@Composable
@ExperimentalPermissionsApi
@androidx.camera.core.ExperimentalGetImage
@AddTrace(name = "camera_screen", enabled = true)
fun CameraScreen(
    modifier: Modifier = Modifier,
    onImageFile: (File) -> Unit = { },
    viewModel: CameraViewModel = hiltViewModel(),
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
                unavailableExplanationResId = R.string.can_not_work_with_no_camera,
            )
        },
    ) {
        Box {
            val lifecycleOwner = LocalLifecycleOwner.current
            val coroutineScope = rememberCoroutineScope()

            // Collect state from ViewModel
            val uiState by viewModel.uiState.collectAsState()
            val configuration by viewModel.configuration.collectAsState()

            var previewUseCase by remember {
                mutableStateOf<UseCase>(
                    Preview.Builder().build(),
                )
            }
            var cameraLens by remember { mutableStateOf(CameraSelector.LENS_FACING_BACK) }

            var recognitionState by remember {
                mutableStateOf(RecognitionState.ACTIVE)
            }

            val imageCaptureUseCase: ImageCapture by remember {
                mutableStateOf(
                    ImageCapture
                        .Builder()
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                        .build(),
                )
            }

            val imageAnalysisUseCase: ImageAnalysis by remember {
                mutableStateOf(
                    ImageAnalysis
                        .Builder()
                        .setTargetResolution(Size(160, 256))
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                        .build(),
                )
            }

            // Set up analyzer to use ViewModel
            imageAnalysisUseCase.setAnalyzer(executor) { imageProxy: ImageProxy ->
                try {
                    if (recognitionState == RecognitionState.ACTIVE && configuration.analysisEnabled) {
                        viewModel.analyzePose(imageProxy)
                    }
                } finally {
                    imageProxy.close()
                }
            }

            Box {
                CameraPreview(
                    modifier = Modifier.fillMaxSize(),
                    onUseCase = {
                        previewUseCase = it
                    },
                )

                // Render UI based on state
                when (val state = uiState) {
                    is CameraUiState.Analyzing -> {
                        if (recognitionState == RecognitionState.ACTIVE) {
                            val predictions = state.analysisResult.predictions.map { it.toUiModel() }

                            // Only show table if we have all expected results
                            if (predictions.size == RecognitionModelName.values().size) {
                                ResultTable(
                                    modifier = Modifier.align(Alignment.TopCenter),
                                    isDetected =
                                        if (state.analysisResult.isDetected) {
                                            PoseDetectionStateResult.DETECTED
                                        } else {
                                            PoseDetectionStateResult.NOT_DETECTED
                                        },
                                    recognitionModelPredictionResults = predictions,
                                )
                            }
                        }
                    }
                    is CameraUiState.Error -> {
                        Timber.e("Camera error: ${state.message}")
                    }
                    else -> {
                        // Idle or CameraReady - no results to display yet
                    }
                }

                CameraControls(
                    modifier =
                        modifier
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
                    },
                )
            }

            LaunchedEffect(previewUseCase, cameraLens) {
                launchedEffects(
                    context,
                    lifecycleOwner,
                    previewUseCase,
                    imageCaptureUseCase,
                    imageAnalysisUseCase,
                    cameraLens,
                )
            }

            // Start analysis when screen appears
            LaunchedEffect(Unit) {
                viewModel.startAnalysis()
            }

            // Stop analysis when screen disappears
            DisposableEffect(Unit) {
                onDispose {
                    viewModel.stopAnalysis()
                }
            }
        }
    }
}

private suspend fun launchedEffects(
    context: Context,
    lifecycleOwner: LifecycleOwner,
    previewUseCase: UseCase,
    imageCaptureUseCase: ImageCapture,
    imageAnalysisUseCase: ImageAnalysis,
    cameraLens: Int,
) {
    val cameraProvider = context.getCameraProvider()
    val cameraSelector =
        CameraSelector
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
            imageCaptureUseCase,
        )
        cameraProvider.bindToLifecycle(
            lifecycleOwner,
            cameraSelector,
            imageAnalysisUseCase,
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
        modifier =
            Modifier
                .size(125.dp)
                .wrapContentSize(),
    ) { contentPadding ->
        CameraScreen(modifier = Modifier.padding(contentPadding))
    }
}
