package com.po4yka.dancer.domain.repository

import com.po4yka.dancer.domain.model.DomainUri
import com.po4yka.dancer.domain.model.PoseAnalysisResult
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for managing image analysis persistence.
 *
 * This repository provides an abstraction layer for storing and retrieving
 * pose analysis results along with their associated image URIs and metadata.
 * It follows the repository pattern to separate domain logic from data access.
 *
 * Key responsibilities:
 * - Persisting analysis results to local storage
 * - Retrieving stored analyses by ID or as a complete list
 * - Managing the lifecycle of stored image data
 * - Providing reactive data access via Flow
 *
 * The repository works with domain models (PoseAnalysisResult) rather than
 * database entities, maintaining clean architecture separation.
 */
interface ImageRepository {
    /**
     * Saves a pose analysis result for an image.
     *
     * This method persists the analysis result along with the image URI and metadata.
     * If an analysis with the same ID already exists, it will be replaced.
     *
     * @param id Unique identifier for this analysis (typically UUID)
     * @param imageUri URI pointing to the analyzed image
     * @param result The pose analysis result containing predictions and confidence
     * @param timestamp When the analysis was performed (milliseconds since epoch)
     * @param cameraLens Which camera lens was used (front/back), null if from gallery
     * @param threshold Detection threshold used for this analysis
     */
    suspend fun saveAnalysis(
        id: String,
        imageUri: DomainUri,
        result: PoseAnalysisResult,
        timestamp: Long = System.currentTimeMillis(),
        cameraLens: String? = null,
        threshold: Float,
    )

    /**
     * Retrieves a specific analysis by its ID.
     *
     * Returns a Flow that emits the analysis data when found.
     * The Flow will emit null if no analysis exists with the given ID.
     *
     * @param id Unique identifier of the analysis to retrieve
     * @return Flow emitting ImageAnalysisData or null if not found
     */
    fun getAnalysis(id: String): Flow<ImageAnalysisData?>

    /**
     * Retrieves all stored analyses ordered by timestamp (most recent first).
     *
     * Returns a Flow that emits the updated list whenever analyses are
     * added, modified, or deleted. Ideal for displaying a gallery/history view.
     *
     * @return Flow emitting list of all stored analyses
     */
    fun getAllAnalyses(): Flow<List<ImageAnalysisData>>

    /**
     * Deletes a specific analysis by its ID.
     *
     * This removes the database record but does not delete the actual image file.
     * Consider implementing image file cleanup separately if needed.
     *
     * @param id Unique identifier of the analysis to delete
     */
    suspend fun deleteAnalysis(id: String)

    /**
     * Deletes all stored analyses.
     *
     * Useful for clearing history or resetting the app.
     * Does not delete the actual image files.
     */
    suspend fun deleteAll()
}

/**
 * Data class representing a complete image analysis record.
 *
 * This domain model combines the image metadata with the analysis result
 * for convenient access in the presentation layer.
 *
 * @property id Unique identifier for this analysis
 * @property imageUri URI pointing to the analyzed image
 * @property timestamp When the analysis was performed (milliseconds since epoch)
 * @property result The pose analysis result containing predictions and confidence
 * @property cameraLens Which camera lens was used (front/back), null if from gallery
 * @property threshold Detection threshold used for this analysis
 */
data class ImageAnalysisData(
    val id: String,
    val imageUri: DomainUri,
    val timestamp: Long,
    val result: PoseAnalysisResult,
    val cameraLens: String?,
    val threshold: Float,
)
