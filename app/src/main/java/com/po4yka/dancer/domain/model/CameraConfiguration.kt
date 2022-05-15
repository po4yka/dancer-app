package com.po4yka.dancer.domain.model

/**
 * Domain model representing camera and analysis configuration.
 *
 * This model encapsulates all configuration settings required for
 * camera-based pose analysis operations.
 *
 * @property threshold The minimum confidence threshold for pose detection.
 *                     Predictions with confidence below this value are considered as "not detected".
 *                     Default: 2.5f (based on current MoveAnalyzer implementation).
 * @property mirrorMode Whether to mirror/flip the camera image horizontally.
 *                      Typically enabled for front-facing camera for natural user experience.
 * @property analysisEnabled Whether pose analysis should be actively running.
 * @property targetWidth The target width for image processing (pixels).
 * @property targetHeight The target height for image processing (pixels).
 */
data class CameraConfiguration(
    val threshold: Float = DEFAULT_THRESHOLD,
    val mirrorMode: Boolean = false,
    val analysisEnabled: Boolean = true,
    val targetWidth: Int = DEFAULT_IMAGE_WIDTH,
    val targetHeight: Int = DEFAULT_IMAGE_HEIGHT,
) {
    init {
        require(threshold >= 0f) { "Threshold must be non-negative" }
        require(targetWidth > 0) { "Target width must be positive" }
        require(targetHeight > 0) { "Target height must be positive" }
    }

    companion object {
        /**
         * Default confidence threshold for pose detection.
         * Matches the current THRESHOLD_VALUE in MoveAnalyzer.
         */
        const val DEFAULT_THRESHOLD = 2.5f

        /**
         * Default image processing dimensions.
         * Matches IMAGE_NET_WIDTH and IMAGE_NEW_HEIGHT from PoseClassifierProcessor.
         */
        const val DEFAULT_IMAGE_WIDTH = 160
        const val DEFAULT_IMAGE_HEIGHT = 256
    }
}
