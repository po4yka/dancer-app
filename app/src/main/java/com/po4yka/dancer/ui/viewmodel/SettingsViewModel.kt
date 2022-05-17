package com.po4yka.dancer.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.po4yka.dancer.domain.model.CameraConfiguration
import com.po4yka.dancer.domain.usecase.GetCameraConfigurationUseCase
import com.po4yka.dancer.domain.usecase.UpdateCameraConfigurationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * ViewModel for the Settings screen managing camera and analysis configuration.
 *
 * This ViewModel provides a reactive interface for viewing and modifying application settings.
 * It acts as the intermediary between the UI and the domain layer, handling all business logic
 * related to configuration management.
 *
 * Features:
 * - Reactive configuration state via StateFlow
 * - Granular update methods for individual settings
 * - Comprehensive error handling with user-friendly messages
 * - Automatic configuration persistence through use cases
 *
 * The ViewModel exposes three main pieces of state:
 * 1. [configuration] - The current camera configuration
 * 2. [isLoading] - Whether a configuration update is in progress
 * 3. [errorMessage] - Any error that occurred during operations
 *
 * Usage in Composable:
 * ```kotlin
 * @Composable
 * fun SettingsScreen(viewModel: SettingsViewModel = hiltViewModel()) {
 *     val config by viewModel.configuration.collectAsState()
 *     val isLoading by viewModel.isLoading.collectAsState()
 *     val error by viewModel.errorMessage.collectAsState()
 *
 *     SettingsContent(
 *         configuration = config,
 *         isLoading = isLoading,
 *         error = error,
 *         onThresholdChange = viewModel::updateThreshold,
 *         onMirrorModeChange = viewModel::updateMirrorMode,
 *         onAnalysisEnabledChange = viewModel::updateAnalysisEnabled,
 *         onErrorDismiss = viewModel::clearError
 *     )
 * }
 * ```
 *
 * @property getCameraConfigurationUseCase Use case for retrieving configuration.
 * @property updateCameraConfigurationUseCase Use case for updating configuration.
 */
@HiltViewModel
class SettingsViewModel
    @Inject
    constructor(
        private val getCameraConfigurationUseCase: GetCameraConfigurationUseCase,
        private val updateCameraConfigurationUseCase: UpdateCameraConfigurationUseCase,
    ) : ViewModel() {
        private val _configuration = MutableStateFlow(CameraConfiguration())

        /**
         * Observable camera configuration state.
         *
         * This flow emits the current configuration and any updates that occur.
         * The UI should collect this to display current settings values.
         */
        val configuration: StateFlow<CameraConfiguration> = _configuration.asStateFlow()

        private val _isLoading = MutableStateFlow(false)

        /**
         * Observable loading state for configuration updates.
         *
         * True when a configuration update is in progress, false otherwise.
         * The UI can use this to show loading indicators or disable controls.
         */
        val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

        private val _errorMessage = MutableStateFlow<String?>(null)

        /**
         * Observable error message state.
         *
         * Non-null when an error occurred during configuration operations.
         * The UI should display this to the user and call [clearError] after acknowledgment.
         */
        val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

        init {
            Timber.d("SettingsViewModel initialized")
            observeConfiguration()
        }

        /**
         * Observes configuration changes from the repository.
         *
         * This method sets up a flow collector that automatically updates the
         * internal configuration state whenever settings change (from any source).
         * This ensures the UI always displays the latest configuration values.
         */
        private fun observeConfiguration() {
            getCameraConfigurationUseCase()
                .onEach { config ->
                    _configuration.value = config
                    Timber.d(
                        "Configuration loaded: threshold=${config.threshold}, " +
                            "mirror=${config.mirrorMode}, enabled=${config.analysisEnabled}",
                    )
                }
                .catch { e ->
                    Timber.e(e, "Error observing configuration")
                    _errorMessage.value = "Failed to load settings: ${e.message}"
                }
                .launchIn(viewModelScope)
        }

        /**
         * Updates the detection threshold setting.
         *
         * The threshold determines the minimum confidence required for pose detection.
         * Higher values require more confidence, reducing false positives but potentially
         * missing valid poses. Lower values increase sensitivity but may produce more
         * false detections.
         *
         * Typical range: 0.5 to 5.0
         * Default: 2.5
         *
         * @param threshold The new threshold value. Must be non-negative.
         * @throws IllegalArgumentException if threshold is negative (handled by use case).
         */
        fun updateThreshold(threshold: Float) {
            viewModelScope.launch {
                _isLoading.value = true
                _errorMessage.value = null
                try {
                    Timber.d("Updating threshold to $threshold")
                    updateCameraConfigurationUseCase.updateThreshold(threshold)
                    Timber.d("Threshold updated successfully")
                } catch (e: IllegalArgumentException) {
                    Timber.e(e, "Invalid threshold value")
                    _errorMessage.value = "Invalid threshold: ${e.message}"
                } catch (e: Exception) {
                    Timber.e(e, "Failed to update threshold")
                    _errorMessage.value = "Failed to update threshold: ${e.message}"
                } finally {
                    _isLoading.value = false
                }
            }
        }

        /**
         * Updates the mirror mode setting.
         *
         * When enabled, the camera image is flipped horizontally, providing a more natural
         * "mirror" experience for the user. This is typically enabled for front-facing cameras
         * and disabled for rear-facing cameras.
         *
         * @param enabled Whether mirror mode should be enabled.
         */
        fun updateMirrorMode(enabled: Boolean) {
            viewModelScope.launch {
                _isLoading.value = true
                _errorMessage.value = null
                try {
                    Timber.d("Updating mirror mode to $enabled")
                    updateCameraConfigurationUseCase.updateMirrorMode(enabled)
                    Timber.d("Mirror mode updated successfully")
                } catch (e: Exception) {
                    Timber.e(e, "Failed to update mirror mode")
                    _errorMessage.value = "Failed to update mirror mode: ${e.message}"
                } finally {
                    _isLoading.value = false
                }
            }
        }

        /**
         * Updates the analysis enabled setting.
         *
         * When enabled, pose analysis runs continuously on camera frames.
         * When disabled, the camera preview remains active but no ML processing occurs,
         * saving battery and computational resources.
         *
         * @param enabled Whether pose analysis should be active.
         */
        fun updateAnalysisEnabled(enabled: Boolean) {
            viewModelScope.launch {
                _isLoading.value = true
                _errorMessage.value = null
                try {
                    Timber.d("Updating analysis enabled to $enabled")
                    updateCameraConfigurationUseCase.updateAnalysisEnabled(enabled)
                    Timber.d("Analysis enabled updated successfully")
                } catch (e: Exception) {
                    Timber.e(e, "Failed to update analysis enabled")
                    _errorMessage.value = "Failed to update analysis state: ${e.message}"
                } finally {
                    _isLoading.value = false
                }
            }
        }

        /**
         * Updates the entire configuration atomically.
         *
         * This method replaces all configuration settings at once, useful for:
         * - Resetting to defaults
         * - Applying preset configurations
         * - Bulk updates from external sources
         *
         * For updating individual settings, prefer the specific methods
         * ([updateThreshold], [updateMirrorMode], [updateAnalysisEnabled]).
         *
         * @param configuration The new configuration to apply.
         */
        fun updateConfiguration(configuration: CameraConfiguration) {
            viewModelScope.launch {
                _isLoading.value = true
                _errorMessage.value = null
                try {
                    Timber.d("Updating entire configuration: $configuration")
                    updateCameraConfigurationUseCase(configuration)
                    Timber.d("Configuration updated successfully")
                } catch (e: Exception) {
                    Timber.e(e, "Failed to update configuration")
                    _errorMessage.value = "Failed to update settings: ${e.message}"
                } finally {
                    _isLoading.value = false
                }
            }
        }

        /**
         * Resets configuration to default values.
         *
         * This will restore all settings to their default values as defined in [CameraConfiguration].
         * Default values:
         * - Threshold: 2.5
         * - Mirror mode: false
         * - Analysis enabled: true
         * - Target dimensions: 160x256
         */
        fun resetToDefaults() {
            viewModelScope.launch {
                _isLoading.value = true
                _errorMessage.value = null
                try {
                    Timber.d("Resetting configuration to defaults")
                    val defaultConfig = CameraConfiguration()
                    updateCameraConfigurationUseCase(defaultConfig)
                    Timber.d("Configuration reset to defaults successfully")
                } catch (e: Exception) {
                    Timber.e(e, "Failed to reset configuration")
                    _errorMessage.value = "Failed to reset settings: ${e.message}"
                } finally {
                    _isLoading.value = false
                }
            }
        }

        /**
         * Clears the current error message.
         *
         * Call this after the user acknowledges an error message (e.g., dismisses a snackbar).
         */
        fun clearError() {
            _errorMessage.value = null
        }

        /**
         * Gets the current configuration synchronously.
         *
         * This provides immediate access to the current configuration value
         * without observing the flow. Useful for one-time reads.
         *
         * @return The current camera configuration.
         */
        fun getCurrentConfiguration(): CameraConfiguration = _configuration.value

        override fun onCleared() {
            super.onCleared()
            Timber.d("SettingsViewModel cleared")
        }
    }
