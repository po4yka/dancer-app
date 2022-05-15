package com.po4yka.dancer.domain.usecase

import com.po4yka.dancer.domain.repository.ConfigurationRepository
import com.po4yka.dancer.domain.repository.PoseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Use case for managing the camera analysis lifecycle.
 *
 * This use case is responsible for:
 * 1. Starting the pose classification service
 * 2. Monitoring configuration changes
 * 3. Managing the analysis state (active/inactive)
 * 4. Providing a reactive stream of configuration updates
 *
 * Unlike [AnalyzePoseUseCase] which processes individual frames,
 * this use case manages the overall analysis session lifecycle.
 *
 * @property poseRepository Repository for pose classification operations.
 * @property configurationRepository Repository for accessing configuration settings.
 */
class StartCameraAnalysisUseCase
    @Inject
    constructor(
        private val poseRepository: PoseRepository,
        private val configurationRepository: ConfigurationRepository,
    ) {
        /**
         * Starts the camera analysis service and returns a Flow of configuration updates.
         *
         * This method:
         * 1. Starts the pose repository (initializes ML model)
         * 2. Returns a Flow that emits configuration changes
         * 3. The Flow can be used to react to configuration updates in real-time
         *
         * The caller is responsible for:
         * - Setting up the camera image analyzer
         * - Calling [AnalyzePoseUseCase] for each frame
         * - Calling [StopCameraAnalysisUseCase] when done
         *
         * @return Flow that emits configuration updates. The Flow will emit the initial
         *         configuration immediately and subsequent updates whenever configuration changes.
         */
        suspend operator fun invoke(): Flow<Boolean> {
            // Start the pose repository to initialize the ML model
            poseRepository.start()

            // Return a Flow that maps configuration to analysis enabled state
            // This allows the UI to reactively enable/disable analysis
            return configurationRepository.getConfiguration()
                .map { config -> config.analysisEnabled }
        }

        /**
         * Checks if the analysis service is currently active.
         *
         * @return true if the service is active and ready for analysis, false otherwise.
         */
        fun isActive(): Boolean {
            return poseRepository.isActive()
        }
    }
