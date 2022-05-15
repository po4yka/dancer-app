package com.po4yka.dancer.ui.viewmodel

import androidx.camera.core.ImageProxy
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.po4yka.dancer.data.adapter.ImageProxyAdapter
import com.po4yka.dancer.domain.model.CameraConfiguration
import com.po4yka.dancer.domain.model.PoseAnalysisResult
import com.po4yka.dancer.domain.usecase.AnalyzePoseUseCase
import com.po4yka.dancer.domain.usecase.GetCameraConfigurationUseCase
import com.po4yka.dancer.domain.usecase.StartCameraAnalysisUseCase
import com.po4yka.dancer.domain.usecase.StopCameraAnalysisUseCase
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
 * UI state representation for the Camera screen.
 *
 * This sealed class represents all possible states of the camera analysis feature.
 * It follows the MVI (Model-View-Intent) pattern for unidirectional data flow.
 *
 * States:
 * - [Idle]: Camera is ready but analysis is not active
 * - [CameraReady]: Camera is initialized and ready to start analysis
 * - [Analyzing]: Analysis is in progress with current results
 * - [Error]: An error occurred during camera or analysis operations
 */
sealed class CameraUiState {
    /**
     * Initial state when camera is ready but analysis hasn't started.
     */
    object Idle : CameraUiState()

    /**
     * Camera is initialized and ready to begin analysis.
     */
    object CameraReady : CameraUiState()

    /**
     * Analysis is actively running with current pose detection results.
     *
     * @property analysisResult The latest pose analysis result from the ML model.
     * @property configuration Current camera configuration including threshold and mirror mode.
     */
    data class Analyzing(
        val analysisResult: PoseAnalysisResult,
        val configuration: CameraConfiguration,
    ) : CameraUiState()

    /**
     * An error occurred during camera initialization or analysis.
     *
     * @property message Human-readable error message to display to the user.
     */
    data class Error(val message: String) : CameraUiState()
}

/**
 * ViewModel for the Camera screen managing pose analysis and camera state.
 *
 * This ViewModel follows MVVM architecture principles and serves as the single source
 * of truth for camera analysis state. It coordinates between multiple use cases and
 * exposes reactive state to the UI layer.
 *
 * Responsibilities:
 * - Managing camera analysis lifecycle (start/stop)
 * - Processing camera frames through the pose analysis pipeline
 * - Managing configuration state (threshold, mirror mode, analysis enabled)
 * - Handling errors and exposing them to the UI
 * - Cleaning up resources on ViewModel destruction
 *
 * The ViewModel uses StateFlow for reactive state management, allowing the UI to
 * observe state changes and react accordingly.
 *
 * Usage in Composable:
 * ```kotlin
 * @Composable
 * fun CameraScreen(viewModel: CameraViewModel = hiltViewModel()) {
 *     val uiState by viewModel.uiState.collectAsState()
 *     val configuration by viewModel.configuration.collectAsState()
 *
 *     LaunchedEffect(Unit) {
 *         viewModel.startAnalysis()
 *     }
 *
 *     DisposableEffect(Unit) {
 *         onDispose { viewModel.stopAnalysis() }
 *     }
 *
 *     // Use uiState to render UI
 *     when (val state = uiState) {
 *         is CameraUiState.Analyzing -> {
 *             // Show analysis results
 *         }
 *         is CameraUiState.Error -> {
 *             // Show error message
 *         }
 *         // ... handle other states
 *     }
 * }
 * ```
 *
 * @property analyzePoseUseCase Use case for analyzing individual camera frames.
 * @property startCameraAnalysisUseCase Use case for initializing the analysis service.
 * @property stopCameraAnalysisUseCase Use case for stopping the analysis service.
 * @property getCameraConfigurationUseCase Use case for retrieving camera configuration.
 * @property imageProxyAdapter Adapter for converting ImageProxy to domain ImageData.
 */
@HiltViewModel
class CameraViewModel
    @Inject
    constructor(
        private val analyzePoseUseCase: AnalyzePoseUseCase,
        private val startCameraAnalysisUseCase: StartCameraAnalysisUseCase,
        private val stopCameraAnalysisUseCase: StopCameraAnalysisUseCase,
        private val getCameraConfigurationUseCase: GetCameraConfigurationUseCase,
        private val imageProxyAdapter: ImageProxyAdapter,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow<CameraUiState>(CameraUiState.Idle)

        /**
         * Observable UI state for the Camera screen.
         *
         * The UI should collect this flow and update accordingly.
         * State changes are emitted on the Main dispatcher, safe for UI consumption.
         */
        val uiState: StateFlow<CameraUiState> = _uiState.asStateFlow()

        private val _configuration = MutableStateFlow(CameraConfiguration())

        /**
         * Observable camera configuration state.
         *
         * Emits configuration updates including threshold, mirror mode, and analysis enabled status.
         * The UI can observe this to react to configuration changes.
         */
        val configuration: StateFlow<CameraConfiguration> = _configuration.asStateFlow()

        private var isAnalysisActive = false

        init {
            Timber.d("CameraViewModel initialized")
            observeConfiguration()
        }

        /**
         * Observes configuration changes from the repository.
         *
         * This method sets up a flow collector that automatically updates the
         * internal configuration state whenever settings change.
         */
        private fun observeConfiguration() {
            getCameraConfigurationUseCase()
                .onEach { config ->
                    _configuration.value = config
                    Timber.d(
                        "Configuration updated: threshold=${config.threshold}, mirror=${config.mirrorMode}, enabled=${config.analysisEnabled}",
                    )

                    // Update UI state based on analysis enabled flag
                    if (!config.analysisEnabled && _uiState.value is CameraUiState.Analyzing) {
                        _uiState.value = CameraUiState.CameraReady
                    }
                }
                .catch { e ->
                    Timber.e(e, "Error observing configuration")
                    _uiState.value = CameraUiState.Error("Failed to load configuration: ${e.message}")
                }
                .launchIn(viewModelScope)
        }

        /**
         * Starts the camera analysis service.
         *
         * This method:
         * 1. Initializes the ML model through the use case
         * 2. Sets up configuration observation
         * 3. Transitions the UI state to [CameraUiState.CameraReady]
         *
         * Should be called when the camera screen becomes visible (e.g., in LaunchedEffect).
         * Call [stopAnalysis] when the screen is disposed to free resources.
         *
         * @throws IllegalStateException if analysis is already active
         */
        fun startAnalysis() {
            Timber.d("[CameraViewModel] ========================================")
            Timber.d("[CameraViewModel] startAnalysis() called")
            if (isAnalysisActive) {
                Timber.w("[CameraViewModel] Analysis already active, ignoring start request")
                Timber.d("[CameraViewModel] ========================================")
                return
            }

            viewModelScope.launch {
                try {
                    Timber.d("[CameraViewModel] Launching camera analysis service...")

                    // Start the analysis service and observe configuration changes
                    startCameraAnalysisUseCase()
                        .onEach { analysisEnabled ->
                            Timber.d("[CameraViewModel] Analysis enabled state changed: $analysisEnabled")
                            if (!analysisEnabled && _uiState.value is CameraUiState.Analyzing) {
                                Timber.d("[CameraViewModel] Analysis disabled, transitioning to CameraReady")
                                _uiState.value = CameraUiState.CameraReady
                            }
                        }
                        .catch { e ->
                            Timber.e(e, "[CameraViewModel] Error in analysis service")
                            _uiState.value = CameraUiState.Error("Analysis service error: ${e.message}")
                            isAnalysisActive = false
                        }
                        .launchIn(viewModelScope)

                    isAnalysisActive = true
                    _uiState.value = CameraUiState.CameraReady
                    Timber.d("[CameraViewModel] ✓ Camera analysis service started successfully")
                    Timber.d("[CameraViewModel] ========================================")
                } catch (e: Exception) {
                    Timber.e(e, "[CameraViewModel] ✗ Failed to start camera analysis")
                    _uiState.value = CameraUiState.Error("Failed to start analysis: ${e.message}")
                    isAnalysisActive = false
                    Timber.d("[CameraViewModel] ========================================")
                }
            }
        }

        /**
         * Stops the camera analysis service and releases resources.
         *
         * This method:
         * 1. Stops the ML model processing
         * 2. Frees memory and resources
         * 3. Transitions UI state to [CameraUiState.Idle]
         *
         * Should be called when:
         * - The camera screen is navigated away from
         * - The app goes to background
         * - User explicitly disables analysis
         *
         * Safe to call multiple times - will only stop if analysis is active.
         */
        fun stopAnalysis() {
            Timber.d("[CameraViewModel] ========================================")
            Timber.d("[CameraViewModel] stopAnalysis() called")
            if (!isAnalysisActive) {
                Timber.d("[CameraViewModel] Analysis not active, ignoring stop request")
                Timber.d("[CameraViewModel] ========================================")
                return
            }

            viewModelScope.launch {
                try {
                    Timber.d("[CameraViewModel] Stopping camera analysis service...")
                    stopCameraAnalysisUseCase()
                    isAnalysisActive = false
                    _uiState.value = CameraUiState.Idle
                    Timber.d("[CameraViewModel] ✓ Camera analysis service stopped successfully")
                    Timber.d("[CameraViewModel] ========================================")
                } catch (e: kotlinx.coroutines.CancellationException) {
                    // Coroutine was cancelled - this is expected during cleanup, don't log as error
                    Timber.d("[CameraViewModel] Stop analysis cancelled - likely due to ViewModel being cleared")
                    isAnalysisActive = false
                    Timber.d("[CameraViewModel] ========================================")
                    throw e // Re-throw CancellationException to properly propagate cancellation
                } catch (e: Exception) {
                    Timber.e(e, "[CameraViewModel] ✗ Error stopping camera analysis")
                    // Still mark as inactive even if stop fails
                    isAnalysisActive = false
                    _uiState.value = CameraUiState.Error("Failed to stop analysis: ${e.message}")
                    Timber.d("[CameraViewModel] ========================================")
                }
            }
        }

        /**
         * Analyzes a single camera frame for pose detection.
         *
         * This method processes an individual frame through the ML model and updates
         * the UI state with the results. It respects the analysis enabled configuration
         * and will skip processing if analysis is disabled.
         *
         * This should be called from the CameraX ImageAnalysis.Analyzer callback:
         * ```kotlin
         * imageAnalysisUseCase.setAnalyzer(executor) { imageProxy ->
         *     viewModel.analyzePose(imageProxy)
         *     imageProxy.close()
         * }
         * ```
         *
         * @param imageProxy The camera image to analyze. Caller is responsible for closing it.
         */
        @androidx.camera.core.ExperimentalGetImage
        fun analyzePose(imageProxy: ImageProxy) {
            // Only analyze if the service is active and analysis is enabled
            if (!isAnalysisActive || !startCameraAnalysisUseCase.isActive()) {
                Timber.v("[CameraViewModel] Skipping frame - analysis not active")
                return
            }

            val config = _configuration.value
            if (!config.analysisEnabled) {
                Timber.v("[CameraViewModel] Skipping frame - analysis disabled in config")
                return
            }

            viewModelScope.launch {
                try {
                    Timber.v("[CameraViewModel] Received camera frame for analysis")
                    // Convert ImageProxy to domain ImageData
                    val imageData = imageProxyAdapter.toDomain(imageProxy)
                    Timber.v("[CameraViewModel] Image converted, calling use case...")

                    val result =
                        analyzePoseUseCase(
                            imageData = imageData,
                            needMirror = config.mirrorMode,
                        )

                    Timber.v("[CameraViewModel] ✓ Analysis complete, updating UI state")
                    _uiState.value =
                        CameraUiState.Analyzing(
                            analysisResult = result,
                            configuration = config,
                        )
                } catch (e: Exception) {
                    Timber.e(e, "[CameraViewModel] ✗ Error analyzing pose")
                    _uiState.value = CameraUiState.Error("Analysis failed: ${e.message}")
                }
            }
        }

        /**
         * Updates the camera configuration.
         *
         * This method is primarily used for testing or programmatic configuration updates.
         * For user-driven settings changes, use [SettingsViewModel] instead.
         *
         * @param configuration The new configuration to apply.
         */
        fun updateConfiguration(configuration: CameraConfiguration) {
            _configuration.value = configuration
            Timber.d("Configuration manually updated: $configuration")
        }

        /**
         * Checks if the analysis service is currently active.
         *
         * @return true if analysis is running, false otherwise.
         */
        fun isActive(): Boolean = isAnalysisActive && startCameraAnalysisUseCase.isActive()

        /**
         * Called when the ViewModel is cleared (screen destroyed, activity finished, etc.).
         *
         * Ensures proper cleanup of resources by stopping the analysis service.
         */
        override fun onCleared() {
            super.onCleared()
            Timber.d("CameraViewModel cleared, stopping analysis")
            if (isAnalysisActive) {
                // Stop synchronously on cleanup
                viewModelScope.launch {
                    stopCameraAnalysisUseCase()
                }
            }
        }
    }
