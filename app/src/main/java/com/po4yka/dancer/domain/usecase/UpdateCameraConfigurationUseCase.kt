package com.po4yka.dancer.domain.usecase

import com.po4yka.dancer.domain.model.CameraConfiguration
import com.po4yka.dancer.domain.repository.ConfigurationRepository
import javax.inject.Inject

/**
 * Use case for updating camera configuration.
 *
 * This use case encapsulates the business logic for modifying camera and
 * analysis configuration settings. It provides a clean API for updating
 * configuration values while maintaining validation and consistency rules.
 *
 * The use case validates inputs where appropriate and delegates persistence
 * to the repository layer.
 *
 * @property configurationRepository Repository for managing configuration settings.
 */
class UpdateCameraConfigurationUseCase
    @Inject
    constructor(
        private val configurationRepository: ConfigurationRepository,
    ) {
        /**
         * Updates the complete camera configuration.
         *
         * This replaces the entire configuration with new values.
         * Use this when multiple settings need to be updated atomically.
         *
         * @param configuration The new configuration to apply.
         */
        suspend operator fun invoke(configuration: CameraConfiguration) {
            configurationRepository.updateConfiguration(configuration)
        }

        /**
         * Updates only the detection threshold.
         *
         * This is useful for threshold tuning without affecting other settings.
         * The threshold determines the minimum confidence required for pose detection.
         *
         * @param threshold The new threshold value. Must be non-negative.
         * @throws IllegalArgumentException if threshold is negative.
         */
        suspend fun updateThreshold(threshold: Float) {
            require(threshold >= 0f) { "Threshold must be non-negative" }
            configurationRepository.updateThreshold(threshold)
        }

        /**
         * Updates the mirror mode setting.
         *
         * Mirror mode flips the camera image horizontally, typically used
         * with front-facing cameras for a more natural user experience.
         *
         * @param enabled Whether mirror mode should be enabled.
         */
        suspend fun updateMirrorMode(enabled: Boolean) {
            configurationRepository.updateMirrorMode(enabled)
        }

        /**
         * Updates the analysis enabled state.
         *
         * This controls whether pose analysis is actively running.
         * Disabling analysis can save battery and processing power.
         *
         * @param enabled Whether pose analysis should be active.
         */
        suspend fun updateAnalysisEnabled(enabled: Boolean) {
            configurationRepository.updateAnalysisEnabled(enabled)
        }
    }
