package com.po4yka.dancer.domain.usecase

import com.po4yka.dancer.domain.repository.ImageRepository
import timber.log.Timber
import javax.inject.Inject

/**
 * Use case for deleting stored image analyses.
 *
 * This use case encapsulates the business logic for removing pose analysis
 * records from persistent storage. Note that this only deletes the database
 * record - the actual image file is not removed.
 *
 * Usage:
 * ```kotlin
 * viewModelScope.launch {
 *     try {
 *         deleteImageUseCase(imageId)
 *         // Navigate back or show success message
 *     } catch (e: Exception) {
 *         // Handle error
 *     }
 * }
 * ```
 *
 * @property imageRepository Repository for managing image analyses
 */
class DeleteImageUseCase
    @Inject
    constructor(
        private val imageRepository: ImageRepository,
    ) {
        /**
         * Deletes a specific image analysis by its ID.
         *
         * This removes the database record including all metadata and analysis results.
         * The actual image file is not deleted - it remains at its original URI.
         * Consider implementing file cleanup separately if needed.
         *
         * @param imageId Unique identifier of the analysis to delete
         * @throws Exception if deletion fails
         */
        suspend operator fun invoke(imageId: String) {
            Timber.d("Deleting image analysis: $imageId")

            try {
                imageRepository.deleteAnalysis(imageId)
                Timber.d("Successfully deleted image analysis: $imageId")
            } catch (e: Exception) {
                Timber.e(e, "Failed to delete image analysis: $imageId")
                throw e
            }
        }

        /**
         * Deletes all stored image analyses.
         *
         * This is useful for:
         * - Clearing history
         * - Resetting the app to initial state
         * - Implementing a "clear all" feature
         *
         * Note: This does not delete the actual image files.
         *
         * @throws Exception if deletion fails
         */
        suspend fun deleteAll() {
            Timber.d("Deleting all image analyses")

            try {
                imageRepository.deleteAll()
                Timber.d("Successfully deleted all image analyses")
            } catch (e: Exception) {
                Timber.e(e, "Failed to delete all image analyses")
                throw e
            }
        }
    }
