package com.po4yka.dancer.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.po4yka.dancer.data.adapter.UriAdapter
import com.po4yka.dancer.domain.model.PoseAnalysisResult
import com.po4yka.dancer.domain.usecase.DeleteImageUseCase
import com.po4yka.dancer.domain.usecase.LoadImageUseCase
import com.po4yka.dancer.domain.usecase.SaveImageAnalysisUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * UI state representation for the Image detail screen.
 *
 * This sealed class represents all possible states when viewing an image
 * and its associated pose analysis results.
 *
 * States:
 * - [Loading]: Image or analysis is being loaded
 * - [Loaded]: Image and analysis results are available
 * - [Error]: An error occurred loading the image or analysis
 */
sealed class ImageUiState {
    /**
     * Initial state or when loading image/analysis data.
     */
    object Loading : ImageUiState()

    /**
     * Image and analysis data successfully loaded.
     *
     * @property imageUri URI of the image to display.
     * @property analysisResult Pose analysis results for the image, if available.
     * @property metadata Additional metadata about the image (capture time, etc.).
     */
    data class Loaded(
        val imageUri: Uri,
        val analysisResult: PoseAnalysisResult?,
        val metadata: ImageMetadata = ImageMetadata(),
    ) : ImageUiState()

    /**
     * An error occurred while loading the image or performing analysis.
     *
     * @property message Human-readable error message.
     */
    data class Error(val message: String) : ImageUiState()
}

/**
 * Metadata associated with an image.
 *
 * @property captureTime Timestamp when the image was captured (millis since epoch).
 * @property cameraLens Which camera lens was used (front/back).
 * @property imageId Unique identifier for the image.
 */
data class ImageMetadata(
    val captureTime: Long? = null,
    val cameraLens: String? = null,
    val imageId: String? = null,
)

/**
 * ViewModel for the Image detail screen managing image display and analysis results.
 *
 * This ViewModel handles the presentation logic for viewing individual images,
 * including displaying the image itself and any associated pose analysis results.
 * It supports both images captured from the camera and images selected from the gallery.
 *
 * Features:
 * - Image loading and display state management
 * - Pose analysis results display
 * - Image metadata management
 * - Error handling for image operations
 *
 * The ViewModel is designed to work with both:
 * 1. Recently captured images from CameraScreen
 * 2. Historical images from GalleryScreen
 *
 * Usage in Composable:
 * ```kotlin
 * @Composable
 * fun ImageScreen(
 *     imageId: String,
 *     viewModel: ImageViewModel = hiltViewModel()
 * ) {
 *     val uiState by viewModel.uiState.collectAsState()
 *
 *     LaunchedEffect(imageId) {
 *         viewModel.loadImage(imageId)
 *     }
 *
 *     when (val state = uiState) {
 *         is ImageUiState.Loading -> {
 *             LoadingIndicator()
 *         }
 *         is ImageUiState.Loaded -> {
 *             ImageContent(
 *                 imageUri = state.imageUri,
 *                 analysisResult = state.analysisResult,
 *                 metadata = state.metadata
 *             )
 *         }
 *         is ImageUiState.Error -> {
 *             ErrorMessage(message = state.message)
 *         }
 *     }
 * }
 * ```
 *
 * Future enhancements:
 * - Image deletion
 * - Sharing functionality
 * - Re-analysis with different thresholds
 * - Image annotation/editing
 */
@HiltViewModel
class ImageViewModel
    @Inject
    constructor(
        private val loadImageUseCase: LoadImageUseCase,
        private val saveImageAnalysisUseCase: SaveImageAnalysisUseCase,
        private val deleteImageUseCase: DeleteImageUseCase,
        private val uriAdapter: UriAdapter,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow<ImageUiState>(ImageUiState.Loading)

        /**
         * Observable UI state for the Image screen.
         *
         * The UI should collect this flow and update accordingly.
         * State changes represent the image loading lifecycle.
         */
        val uiState: StateFlow<ImageUiState> = _uiState.asStateFlow()

        init {
            Timber.d("ImageViewModel initialized")
        }

        /**
         * Loads an image by its identifier.
         *
         * This method retrieves the image and its associated analysis results from storage.
         * It transitions through Loading -> Loaded/Error states.
         *
         * The method:
         * 1. Queries the image repository for the image URI
         * 2. Loads any stored analysis results
         * 3. Retrieves metadata (capture time, camera info)
         *
         * @param imageId Unique identifier for the image to load.
         */
        fun loadImage(imageId: String) {
            _uiState.value = ImageUiState.Loading
            Timber.d("Loading image: $imageId")

            viewModelScope.launch {
                loadImageUseCase(imageId)
                    .catch { e ->
                        Timber.e(e, "Failed to load image: $imageId")
                        _uiState.value = ImageUiState.Error("Failed to load image: ${e.message}")
                    }
                    .collect { analysisData ->
                        if (analysisData != null) {
                            // Convert DomainUri back to Android Uri for UI display
                            val androidUri = uriAdapter.toAndroid(analysisData.imageUri)
                            _uiState.value =
                                ImageUiState.Loaded(
                                    imageUri = androidUri,
                                    analysisResult = analysisData.result,
                                    metadata =
                                        ImageMetadata(
                                            captureTime = analysisData.timestamp,
                                            cameraLens = analysisData.cameraLens,
                                            imageId = imageId,
                                        ),
                                )
                            Timber.d("Image loaded successfully: $imageId")
                        } else {
                            _uiState.value = ImageUiState.Error("Image not found: $imageId")
                            Timber.w("Image not found: $imageId")
                        }
                    }
            }
        }

        /**
         * Loads an image directly from a URI.
         *
         * This is useful for displaying images immediately after capture,
         * without persisting them to a database first.
         *
         * @param imageUri URI of the image to display.
         * @param analysisResult Optional pose analysis result to display with the image.
         * @param metadata Optional metadata about the image.
         */
        fun loadImageFromUri(
            imageUri: Uri,
            analysisResult: PoseAnalysisResult? = null,
            metadata: ImageMetadata = ImageMetadata(),
        ) {
            Timber.d("Loading image from URI: $imageUri")
            _uiState.value =
                ImageUiState.Loaded(
                    imageUri = imageUri,
                    analysisResult = analysisResult,
                    metadata = metadata,
                )
        }

        /**
         * Saves the current image analysis to persistent storage.
         *
         * This method persists the analysis result along with image metadata,
         * allowing it to be retrieved later from the gallery or history.
         *
         * @param imageUri URI of the image to save
         * @param result The pose analysis result to save
         * @param cameraLens Which camera lens was used (front/back), null if from gallery
         * @param threshold Detection threshold used for this analysis
         * @return The unique ID of the saved analysis, or null if save failed
         */
        suspend fun saveImageAnalysis(
            imageUri: Uri,
            result: PoseAnalysisResult,
            cameraLens: String? = null,
            threshold: Float,
        ): String? {
            Timber.d("Saving image analysis: $imageUri")

            return try {
                // Convert Android Uri to DomainUri
                val domainUri = uriAdapter.toDomain(imageUri)
                val analysisId =
                    saveImageAnalysisUseCase(
                        imageUri = domainUri,
                        result = result,
                        cameraLens = cameraLens,
                        threshold = threshold,
                    )
                Timber.d("Image analysis saved with ID: $analysisId")
                analysisId
            } catch (e: Exception) {
                Timber.e(e, "Failed to save image analysis")
                _uiState.value = ImageUiState.Error("Failed to save analysis: ${e.message}")
                null
            }
        }

        /**
         * Analyzes or re-analyzes the current image for pose detection.
         *
         * This is a placeholder for future pose analysis functionality.
         * Currently, pose analysis is performed in CameraViewModel during capture.
         * This method could be used for:
         * - Re-analyzing with different threshold values
         * - Analyzing gallery images that weren't previously analyzed
         * - Refreshing stale analysis results
         *
         * @param imageUri URI of the image to analyze.
         */
        fun analyzeImage(imageUri: Uri) {
            Timber.d("Analyzing image: $imageUri")
            // Future: Implement on-demand image analysis
            // This would require integrating with AnalyzePoseUseCase
            // to perform analysis on stored images
        }

        /**
         * Deletes the currently displayed image.
         *
         * This method:
         * 1. Removes the analysis record from the database
         * 2. Invokes the callback on successful deletion
         *
         * Note: This does not delete the actual image file, only the database record.
         * Consider implementing file cleanup separately if needed.
         *
         * @param imageId Unique identifier for the image to delete.
         * @param onDeleted Callback invoked after successful deletion.
         */
        fun deleteImage(
            imageId: String,
            onDeleted: () -> Unit = {},
        ) {
            Timber.d("Deleting image: $imageId")

            viewModelScope.launch {
                try {
                    deleteImageUseCase(imageId)
                    Timber.d("Image deleted successfully: $imageId")
                    onDeleted()
                } catch (e: Exception) {
                    Timber.e(e, "Failed to delete image")
                    _uiState.value = ImageUiState.Error("Failed to delete image: ${e.message}")
                }
            }
        }

        /**
         * Clears the error state.
         *
         * Call this after the user acknowledges an error message.
         */
        fun clearError() {
            if (_uiState.value is ImageUiState.Error) {
                _uiState.value = ImageUiState.Loading
            }
        }

        /**
         * Resets the ViewModel state to initial loading state.
         *
         * Useful when navigating back to the image selection screen.
         */
        fun reset() {
            Timber.d("Resetting ImageViewModel state")
            _uiState.value = ImageUiState.Loading
        }

        override fun onCleared() {
            super.onCleared()
            Timber.d("ImageViewModel cleared")
        }
    }
