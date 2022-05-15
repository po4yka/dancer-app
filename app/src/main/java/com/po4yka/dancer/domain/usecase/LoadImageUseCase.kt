package com.po4yka.dancer.domain.usecase

import com.po4yka.dancer.domain.repository.ImageAnalysisData
import com.po4yka.dancer.domain.repository.ImageRepository
import kotlinx.coroutines.flow.Flow
import timber.log.Timber
import javax.inject.Inject

/**
 * Use case for loading a stored image analysis by its ID.
 *
 * This use case encapsulates the business logic for retrieving a previously
 * saved pose analysis result, including the image URI and all metadata.
 *
 * Usage:
 * ```kotlin
 * viewModelScope.launch {
 *     loadImageUseCase(imageId).collect { data ->
 *         if (data != null) {
 *             // Display image and analysis
 *         } else {
 *             // Handle not found
 *         }
 *     }
 * }
 * ```
 *
 * @property imageRepository Repository for accessing stored image analyses
 */
class LoadImageUseCase
    @Inject
    constructor(
        private val imageRepository: ImageRepository,
    ) {
        /**
         * Loads an image analysis by its unique identifier.
         *
         * Returns a Flow that emits the analysis data when found, or null if
         * no analysis exists with the given ID. The Flow will emit updates if
         * the analysis is modified while being observed.
         *
         * @param imageId Unique identifier of the image analysis to load
         * @return Flow emitting ImageAnalysisData or null if not found
         */
        operator fun invoke(imageId: String): Flow<ImageAnalysisData?> {
            Timber.d("Loading image analysis: $imageId")
            return imageRepository.getAnalysis(imageId)
        }

        /**
         * Loads all stored image analyses.
         *
         * Returns a Flow that emits the complete list of analyses, ordered by
         * timestamp (most recent first). Useful for displaying a gallery or history view.
         *
         * @return Flow emitting list of all stored analyses
         */
        fun loadAll(): Flow<List<ImageAnalysisData>> {
            Timber.d("Loading all image analyses")
            return imageRepository.getAllAnalyses()
        }
    }
