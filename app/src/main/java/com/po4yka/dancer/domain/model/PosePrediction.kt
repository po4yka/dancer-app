package com.po4yka.dancer.domain.model

/**
 * Represents a single pose prediction with its associated probability.
 *
 * This is a pure domain model that represents the classification result
 * for a specific dance move or pose.
 *
 * @property moveName The identifier of the predicted dance move/pose.
 * @property probability The probability score for this prediction (0.0 to 1.0).
 *                       Higher values indicate stronger confidence.
 *                       Probabilities are normalized using softmax and sum to 1.0 across all classes.
 */
data class PosePrediction(
    val moveName: String,
    val probability: Float,
) {
    init {
        require(probability >= 0f) { "Probability must be non-negative" }
    }
}
