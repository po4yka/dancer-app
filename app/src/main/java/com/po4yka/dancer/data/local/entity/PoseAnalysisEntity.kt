package com.po4yka.dancer.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity representing a stored pose analysis result.
 *
 * This entity stores all information about a pose analysis operation,
 * including the image URI, analysis results, and metadata. The predictions
 * are stored as a JSON string for simplicity.
 *
 * @property id Unique identifier for the analysis (typically UUID)
 * @property imageUri URI string pointing to the analyzed image
 * @property timestamp When the analysis was performed (milliseconds since epoch)
 * @property isDetected Whether a pose was detected above threshold
 * @property confidence Confidence score of the best prediction (0.0 to 1.0)
 * @property predictions JSON string representation of List<PosePrediction>
 * @property cameraLens Which camera lens was used (front/back), null if from gallery
 * @property threshold Detection threshold used for this analysis
 */
@Entity(tableName = "pose_analyses")
data class PoseAnalysisEntity(
    @PrimaryKey
    val id: String,
    val imageUri: String,
    val timestamp: Long,
    val isDetected: Boolean,
    val confidence: Float,
    // JSON of List<PosePrediction>
    val predictions: String,
    val cameraLens: String?,
    val threshold: Float,
)
