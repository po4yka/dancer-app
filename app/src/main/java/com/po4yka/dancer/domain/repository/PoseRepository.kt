package com.po4yka.dancer.domain.repository

import com.po4yka.dancer.domain.model.ImageData
import com.po4yka.dancer.domain.model.PosePrediction

/**
 * Repository interface for pose classification operations.
 *
 * This interface defines the contract for accessing pose classification capabilities.
 * The actual implementation will be provided by the data layer, which will handle
 * TensorFlow Lite model loading, inference, and resource management.
 *
 * The repository abstracts away the underlying ML framework details and provides
 * a clean domain-level API for pose classification.
 */
interface PoseRepository {
    /**
     * Classifies a pose from the provided image.
     *
     * This method performs ML inference on the given image to identify dance moves/poses.
     * The implementation should handle:
     * - Image preprocessing (rotation, resizing, normalization)
     * - Optional mirroring for front-facing camera
     * - TensorFlow Lite model inference
     * - Result post-processing and mapping
     *
     * @param imageData The image data to analyze. Must contain valid image data.
     * @param needMirror Whether to flip the image horizontally before analysis.
     *                   Typically true for front-facing camera.
     * @return List of pose predictions with probabilities, sorted by confidence (descending).
     *         Returns empty list if classification fails or no image is available.
     */
    suspend fun classifyPose(
        imageData: ImageData,
        needMirror: Boolean,
    ): List<PosePrediction>

    /**
     * Starts the pose classification service.
     *
     * Initializes the ML model and prepares resources for classification.
     * Must be called before [classifyPose].
     */
    suspend fun start()

    /**
     * Stops the pose classification service and releases resources.
     *
     * Closes the ML model and frees memory. After calling this,
     * [start] must be called again before classification can resume.
     */
    suspend fun stop()

    /**
     * Checks if the pose classification service is currently active.
     *
     * @return true if the service is started and ready for classification, false otherwise.
     */
    fun isActive(): Boolean
}
