package com.po4yka.dancer.domain.usecase

import com.po4yka.dancer.domain.model.ImageData
import com.po4yka.dancer.domain.model.PoseAnalysisResult
import com.po4yka.dancer.domain.repository.ConfigurationRepository
import com.po4yka.dancer.domain.repository.PoseRepository
import timber.log.Timber
import javax.inject.Inject

/**
 * Use case for analyzing a single pose from a camera image.
 *
 * This use case encapsulates the business logic for pose analysis:
 * 1. Retrieves current configuration (threshold, mirror mode)
 * 2. Performs pose classification via the repository
 * 3. Applies threshold logic to determine detection status
 * 4. Returns a structured result with detection state and predictions
 *
 * The use case follows the Single Responsibility Principle by focusing solely
 * on the pose analysis workflow, delegating model inference to the repository
 * and configuration management to the configuration repository.
 *
 * @property poseRepository Repository for pose classification operations.
 * @property configurationRepository Repository for accessing configuration settings.
 */
class AnalyzePoseUseCase
    @Inject
    constructor(
        private val poseRepository: PoseRepository,
        private val configurationRepository: ConfigurationRepository,
    ) {
        /**
         * Analyzes a pose from the provided camera image.
         *
         * This method performs the complete pose analysis workflow:
         * - Retrieves the current detection threshold
         * - Classifies the pose using the ML model
         * - Applies threshold logic to determine if a pose is detected
         * - Packages results into a domain model
         *
         * @param imageData The image data to analyze.
         * @param needMirror Whether to mirror the image (typically true for front camera).
         * @return [PoseAnalysisResult] containing detection status, confidence, and predictions.
         *         Returns empty result if classification fails.
         */
        suspend operator fun invoke(
            imageData: ImageData,
            needMirror: Boolean,
        ): PoseAnalysisResult {
            try {
                // Get current configuration for threshold value
                val configuration = configurationRepository.getCurrentConfiguration()

                // Classify the pose using the repository
                val predictions =
                    poseRepository.classifyPose(
                        imageData = imageData,
                        needMirror = needMirror,
                    )

                // Handle empty results
                if (predictions.isEmpty()) {
                    return PoseAnalysisResult.empty()
                }

                // Get the highest confidence prediction
                val topPrediction = predictions.first() // Already sorted by repository
                val confidence = topPrediction.probability

                // Apply threshold logic to determine detection status
                val isDetected = confidence > configuration.threshold

                return PoseAnalysisResult(
                    isDetected = isDetected,
                    confidence = confidence,
                    predictions = predictions,
                )
            } catch (e: Exception) {
                // Log error and return empty result
                Timber.e(e, "Error analyzing pose: ${e.message}")
                // In a production app, you might want to propagate specific exceptions
                // or use a Result wrapper to communicate errors to the caller
                return PoseAnalysisResult.empty()
            }
        }
    }
