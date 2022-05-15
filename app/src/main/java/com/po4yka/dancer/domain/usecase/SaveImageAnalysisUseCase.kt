package com.po4yka.dancer.domain.usecase

import com.po4yka.dancer.domain.model.DomainUri
import com.po4yka.dancer.domain.model.PoseAnalysisResult
import com.po4yka.dancer.domain.repository.ImageRepository
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject

/**
 * Use case for saving pose analysis results to persistent storage.
 *
 * This use case encapsulates the business logic for:
 * - Generating unique IDs for new analyses
 * - Persisting analysis results with image URIs
 * - Storing metadata (timestamp, camera info, threshold)
 *
 * Usage:
 * ```kotlin
 * viewModelScope.launch {
 *     val analysisId = saveImageAnalysisUseCase(
 *         imageUri = capturedImageUri,
 *         result = analysisResult,
 *         cameraLens = "back",
 *         threshold = 0.7f
 *     )
 *     // Use analysisId for navigation or further operations
 * }
 * ```
 *
 * @property imageRepository Repository for storing image analyses
 */
class SaveImageAnalysisUseCase
    @Inject
    constructor(
        private val imageRepository: ImageRepository,
    ) {
        /**
         * Saves a pose analysis result to persistent storage.
         *
         * This method generates a unique ID for the analysis and stores all
         * relevant data including the image URI, analysis results, and metadata.
         * The analysis can later be retrieved using the returned ID.
         *
         * @param imageUri URI pointing to the analyzed image
         * @param result The pose analysis result containing predictions and confidence
         * @param cameraLens Which camera lens was used (e.g., "front", "back"), null if from gallery
         * @param threshold Detection threshold used for this analysis
         * @param timestamp When the analysis was performed (defaults to current time)
         * @return Unique identifier (UUID) for the saved analysis
         */
        suspend operator fun invoke(
            imageUri: DomainUri,
            result: PoseAnalysisResult,
            cameraLens: String? = null,
            threshold: Float,
            timestamp: Long = System.currentTimeMillis(),
        ): String {
            val analysisId = UUID.randomUUID().toString()

            Timber.d(
                "Saving image analysis: id=$analysisId, detected=${result.isDetected}, " +
                    "confidence=${result.confidence}, predictions=${result.predictions.size}",
            )

            try {
                imageRepository.saveAnalysis(
                    id = analysisId,
                    imageUri = imageUri,
                    result = result,
                    timestamp = timestamp,
                    cameraLens = cameraLens,
                    threshold = threshold,
                )

                Timber.d("Successfully saved image analysis: $analysisId")
                return analysisId
            } catch (e: Exception) {
                Timber.e(e, "Failed to save image analysis")
                throw e
            }
        }

        /**
         * Updates an existing analysis with a new result.
         *
         * This is useful for re-analyzing an image with different parameters
         * or updating the analysis after manual corrections.
         *
         * @param analysisId ID of the existing analysis to update
         * @param imageUri URI pointing to the analyzed image
         * @param result The updated pose analysis result
         * @param cameraLens Which camera lens was used
         * @param threshold Detection threshold used for this analysis
         * @param timestamp Timestamp for the update (defaults to current time)
         */
        suspend fun update(
            analysisId: String,
            imageUri: DomainUri,
            result: PoseAnalysisResult,
            cameraLens: String?,
            threshold: Float,
            timestamp: Long = System.currentTimeMillis(),
        ) {
            Timber.d("Updating image analysis: $analysisId")

            try {
                imageRepository.saveAnalysis(
                    id = analysisId,
                    imageUri = imageUri,
                    result = result,
                    timestamp = timestamp,
                    cameraLens = cameraLens,
                    threshold = threshold,
                )

                Timber.d("Successfully updated image analysis: $analysisId")
            } catch (e: Exception) {
                Timber.e(e, "Failed to update image analysis")
                throw e
            }
        }
    }
