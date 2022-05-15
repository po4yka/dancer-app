package com.po4yka.dancer.domain.repository

import com.po4yka.dancer.domain.model.CameraConfiguration
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for managing application configuration.
 *
 * This interface defines the contract for accessing and modifying
 * camera and analysis configuration settings. The implementation
 * will be provided by the data layer and may use SharedPreferences,
 * DataStore, or other persistence mechanisms.
 */
interface ConfigurationRepository {
    /**
     * Gets the current camera configuration as a Flow.
     *
     * This allows observing configuration changes in real-time.
     * The Flow will emit a new value whenever any configuration setting changes.
     *
     * @return Flow that emits the current configuration state.
     */
    fun getConfiguration(): Flow<CameraConfiguration>

    /**
     * Gets the current camera configuration synchronously.
     *
     * @return The current camera configuration.
     */
    suspend fun getCurrentConfiguration(): CameraConfiguration

    /**
     * Updates the camera configuration.
     *
     * This will persist the new configuration and notify all observers
     * via the Flow returned by [getConfiguration].
     *
     * @param configuration The new configuration to apply.
     */
    suspend fun updateConfiguration(configuration: CameraConfiguration)

    /**
     * Updates the detection threshold value.
     *
     * @param threshold The new threshold value. Must be non-negative.
     */
    suspend fun updateThreshold(threshold: Float)

    /**
     * Updates the mirror mode setting.
     *
     * @param enabled Whether mirror mode should be enabled.
     */
    suspend fun updateMirrorMode(enabled: Boolean)

    /**
     * Updates the analysis enabled state.
     *
     * @param enabled Whether pose analysis should be active.
     */
    suspend fun updateAnalysisEnabled(enabled: Boolean)
}
