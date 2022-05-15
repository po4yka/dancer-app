package com.po4yka.dancer.domain.usecase

import com.po4yka.dancer.domain.model.CameraConfiguration
import com.po4yka.dancer.domain.repository.ConfigurationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for retrieving camera configuration.
 *
 * This use case provides access to camera and analysis configuration settings.
 * It offers both reactive (Flow) and one-shot access patterns to accommodate
 * different use cases in the presentation layer.
 *
 * The use case abstracts the configuration source (SharedPreferences, DataStore, etc.)
 * and provides a clean domain-level API for configuration access.
 *
 * @property configurationRepository Repository for accessing configuration settings.
 */
class GetCameraConfigurationUseCase
    @Inject
    constructor(
        private val configurationRepository: ConfigurationRepository,
    ) {
        /**
         * Gets the camera configuration as a reactive Flow.
         *
         * This is useful for:
         * - UI components that need to react to configuration changes
         * - Settings screens that display current values
         * - Real-time synchronization of configuration across components
         *
         * The Flow will emit:
         * - The initial configuration immediately upon collection
         * - Updated configuration whenever any setting changes
         *
         * @return Flow that emits configuration updates.
         */
        operator fun invoke(): Flow<CameraConfiguration> {
            return configurationRepository.getConfiguration()
        }

        /**
         * Gets the current camera configuration as a one-shot operation.
         *
         * This is useful for:
         * - One-time configuration reads
         * - Operations that need current state without observing changes
         * - Initialization logic that requires configuration values
         *
         * @return The current camera configuration.
         */
        suspend fun getCurrent(): CameraConfiguration {
            return configurationRepository.getCurrentConfiguration()
        }
    }
