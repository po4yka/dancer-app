package com.po4yka.dancer.domain.model

/**
 * Represents the result of a pose analysis operation.
 *
 * This domain model encapsulates all information about a single pose analysis,
 * including detection status, confidence level, and individual predictions for each pose.
 *
 * @property isDetected Whether a pose was detected above the configured threshold.
 * @property confidence The confidence score of the best prediction (0.0 to 1.0).
 * @property predictions List of all pose predictions sorted by probability (descending).
 */
data class PoseAnalysisResult(
    val isDetected: Boolean,
    val confidence: Float,
    val predictions: List<PosePrediction>,
) {
    companion object {
        /**
         * Creates an empty result when no pose is detected or analysis fails.
         */
        fun empty(): PoseAnalysisResult =
            PoseAnalysisResult(
                isDetected = false,
                confidence = 0f,
                predictions = emptyList(),
            )
    }
}
