package com.po4yka.dancer.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import com.po4yka.dancer.domain.model.CameraConfiguration
import com.po4yka.dancer.domain.repository.ConfigurationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject

/**
 * Implementation of [ConfigurationRepository] using Jetpack DataStore Preferences.
 *
 * This implementation provides persistent storage for camera and analysis configuration
 * using DataStore, which offers type-safe, asynchronous data storage with Flow support.
 * All operations are thread-safe and reactive.
 *
 * Key features:
 * - Reactive configuration updates via Flow
 * - Type-safe preference keys
 * - Automatic error handling and recovery with default values
 * - Input validation for all configuration parameters
 * - Thread-safe read/write operations
 *
 * @property dataStore The DataStore instance for preferences storage
 */
class ConfigurationRepositoryImpl
    @Inject
    constructor(
        private val dataStore: DataStore<Preferences>,
    ) : ConfigurationRepository {
        /**
         * Preference keys for storing configuration values.
         *
         * These keys are used to store and retrieve configuration data from DataStore.
         * Each key is type-safe and ensures the correct data type is used.
         */
        private object PreferencesKeys {
            val THRESHOLD = floatPreferencesKey("threshold")
            val MIRROR_MODE = booleanPreferencesKey("mirror_mode")
            val ANALYSIS_ENABLED = booleanPreferencesKey("analysis_enabled")
            val TARGET_WIDTH = intPreferencesKey("target_width")
            val TARGET_HEIGHT = intPreferencesKey("target_height")
        }

        /**
         * Gets the current camera configuration as a Flow.
         *
         * This Flow emits a new configuration whenever any setting changes.
         * If an error occurs reading from DataStore, it logs the error and emits
         * the default configuration.
         *
         * @return Flow that emits the current configuration state.
         */
        override fun getConfiguration(): Flow<CameraConfiguration> {
            return dataStore.data
                .catch { exception ->
                    Timber.e(exception, "Error reading configuration from DataStore")
                    // Emit default configuration on error
                    emit(androidx.datastore.preferences.core.emptyPreferences())
                }
                .map { preferences ->
                    mapPreferencesToConfiguration(preferences)
                }
        }

        /**
         * Gets the current camera configuration synchronously.
         *
         * This suspends until the current configuration is retrieved from DataStore.
         * If an error occurs, it logs the error and returns the default configuration.
         *
         * @return The current camera configuration.
         */
        override suspend fun getCurrentConfiguration(): CameraConfiguration {
            return try {
                val preferences = dataStore.data.first()
                mapPreferencesToConfiguration(preferences)
            } catch (exception: Exception) {
                Timber.e(exception, "Error reading current configuration from DataStore")
                CameraConfiguration() // Return default configuration on error
            }
        }

        /**
         * Updates the camera configuration.
         *
         * This validates all input parameters and persists the new configuration to DataStore.
         * All observers via [getConfiguration] will be notified of the change.
         *
         * @param configuration The new configuration to apply.
         * @throws IllegalArgumentException if any configuration parameter is invalid.
         */
        override suspend fun updateConfiguration(configuration: CameraConfiguration) {
            // Validation is performed by CameraConfiguration's init block
            // Additional validation can be added here if needed
            validateConfiguration(configuration)

            try {
                dataStore.edit { preferences ->
                    preferences[PreferencesKeys.THRESHOLD] = configuration.threshold
                    preferences[PreferencesKeys.MIRROR_MODE] = configuration.mirrorMode
                    preferences[PreferencesKeys.ANALYSIS_ENABLED] = configuration.analysisEnabled
                    preferences[PreferencesKeys.TARGET_WIDTH] = configuration.targetWidth
                    preferences[PreferencesKeys.TARGET_HEIGHT] = configuration.targetHeight
                }
                Timber.d("Configuration updated successfully: $configuration")
            } catch (exception: Exception) {
                Timber.e(exception, "Error updating configuration in DataStore")
                throw exception
            }
        }

        /**
         * Updates the detection threshold value.
         *
         * This updates only the threshold field while preserving other settings.
         *
         * @param threshold The new threshold value. Must be non-negative.
         * @throws IllegalArgumentException if threshold is negative.
         */
        override suspend fun updateThreshold(threshold: Float) {
            require(threshold >= 0f) { "Threshold must be non-negative, got: $threshold" }

            try {
                dataStore.edit { preferences ->
                    preferences[PreferencesKeys.THRESHOLD] = threshold
                }
                Timber.d("Threshold updated to: $threshold")
            } catch (exception: Exception) {
                Timber.e(exception, "Error updating threshold in DataStore")
                throw exception
            }
        }

        /**
         * Updates the mirror mode setting.
         *
         * This updates only the mirror mode field while preserving other settings.
         *
         * @param enabled Whether mirror mode should be enabled.
         */
        override suspend fun updateMirrorMode(enabled: Boolean) {
            try {
                dataStore.edit { preferences ->
                    preferences[PreferencesKeys.MIRROR_MODE] = enabled
                }
                Timber.d("Mirror mode updated to: $enabled")
            } catch (exception: Exception) {
                Timber.e(exception, "Error updating mirror mode in DataStore")
                throw exception
            }
        }

        /**
         * Updates the analysis enabled state.
         *
         * This updates only the analysis enabled field while preserving other settings.
         *
         * @param enabled Whether pose analysis should be active.
         */
        override suspend fun updateAnalysisEnabled(enabled: Boolean) {
            try {
                dataStore.edit { preferences ->
                    preferences[PreferencesKeys.ANALYSIS_ENABLED] = enabled
                }
                Timber.d("Analysis enabled updated to: $enabled")
            } catch (exception: Exception) {
                Timber.e(exception, "Error updating analysis enabled in DataStore")
                throw exception
            }
        }

        /**
         * Maps DataStore preferences to a CameraConfiguration object.
         *
         * If any preference is not found, the default value from [CameraConfiguration.Companion]
         * is used.
         *
         * @param preferences The preferences to map.
         * @return A CameraConfiguration with values from preferences or defaults.
         */
        private fun mapPreferencesToConfiguration(preferences: Preferences): CameraConfiguration {
            return CameraConfiguration(
                threshold =
                    preferences[PreferencesKeys.THRESHOLD]
                        ?: CameraConfiguration.DEFAULT_THRESHOLD,
                mirrorMode =
                    preferences[PreferencesKeys.MIRROR_MODE]
                        ?: false,
                analysisEnabled =
                    preferences[PreferencesKeys.ANALYSIS_ENABLED]
                        ?: true,
                targetWidth =
                    preferences[PreferencesKeys.TARGET_WIDTH]
                        ?: CameraConfiguration.DEFAULT_IMAGE_WIDTH,
                targetHeight =
                    preferences[PreferencesKeys.TARGET_HEIGHT]
                        ?: CameraConfiguration.DEFAULT_IMAGE_HEIGHT,
            )
        }

        /**
         * Validates a configuration object.
         *
         * This performs additional validation beyond the CameraConfiguration's init block
         * to ensure data integrity before persistence.
         *
         * @param configuration The configuration to validate.
         * @throws IllegalArgumentException if any parameter is invalid.
         */
        private fun validateConfiguration(configuration: CameraConfiguration) {
            require(configuration.threshold >= 0f) {
                "Threshold must be non-negative, got: ${configuration.threshold}"
            }
            require(configuration.targetWidth > 0) {
                "Target width must be positive, got: ${configuration.targetWidth}"
            }
            require(configuration.targetHeight > 0) {
                "Target height must be positive, got: ${configuration.targetHeight}"
            }
        }
    }
