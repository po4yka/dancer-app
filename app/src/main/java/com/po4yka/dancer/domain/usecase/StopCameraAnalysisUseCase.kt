package com.po4yka.dancer.domain.usecase

import com.po4yka.dancer.domain.repository.PoseRepository
import javax.inject.Inject

/**
 * Use case for stopping the camera analysis service.
 *
 * This use case is responsible for:
 * 1. Stopping the pose classification service
 * 2. Releasing ML model resources
 * 3. Cleaning up any active analysis operations
 *
 * This should be called when:
 * - The camera screen is closed or navigated away from
 * - The app goes to background
 * - Analysis is explicitly disabled by the user
 * - The activity/fragment is being destroyed
 *
 * @property poseRepository Repository for pose classification operations.
 */
class StopCameraAnalysisUseCase
    @Inject
    constructor(
        private val poseRepository: PoseRepository,
    ) {
        /**
         * Stops the camera analysis service and releases resources.
         *
         * This method:
         * 1. Stops the pose repository (closes ML model)
         * 2. Frees memory and resources
         * 3. Ensures clean shutdown of analysis operations
         *
         * After calling this, [StartCameraAnalysisUseCase] must be called again
         * before analysis can resume.
         */
        suspend operator fun invoke() {
            poseRepository.stop()
        }
    }
