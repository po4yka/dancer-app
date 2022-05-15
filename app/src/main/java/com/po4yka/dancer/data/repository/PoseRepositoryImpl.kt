package com.po4yka.dancer.data.repository

import com.po4yka.dancer.data.datasource.PoseClassifierDataSource
import com.po4yka.dancer.di.IoDispatcher
import com.po4yka.dancer.domain.model.ImageData
import com.po4yka.dancer.domain.model.PosePrediction
import com.po4yka.dancer.domain.repository.PoseRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

/**
 * Implementation of the PoseRepository interface using TensorFlow Lite.
 *
 * This class provides pose recognition functionality by coordinating between
 * the data source (PoseClassifierDataSource) and the TensorFlow Lite model.
 * It delegates the actual ML operations to the injected data source, following
 * proper dependency injection principles.
 *
 * Thread Safety:
 * - The classify operation is performed on the Default dispatcher for CPU-intensive work
 * - Start/stop operations use the IO dispatcher for initialization/cleanup
 * - Access to the active state is synchronized to prevent race conditions
 *
 * @property dataSource The data source responsible for performing actual pose classification
 * @property ioDispatcher Coroutine dispatcher for IO-bound operations
 */
class PoseRepositoryImpl
    @Inject
    constructor(
        private val dataSource: PoseClassifierDataSource,
        @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    ) : PoseRepository {
        /**
         * Indicates whether the repository is currently active and ready for classification.
         */
        @Volatile
        private var active = false

        /**
         * Lock object for synchronizing access to the active state.
         */
        private val lock = Any()

        /**
         * Classifies a pose in the provided image asynchronously.
         *
         * This method delegates to the data source for actual classification.
         * The operation is performed on the Default dispatcher to avoid blocking
         * the UI thread, as image processing and model inference are CPU-intensive.
         *
         * @param imageData The image data to analyze
         * @param needMirror Whether to mirror the image horizontally
         * @return List of pose predictions with probabilities, sorted by confidence (descending).
         *         Returns empty list if classification fails or no image is available.
         */
        override suspend fun classifyPose(
            imageData: ImageData,
            needMirror: Boolean,
        ): List<PosePrediction> =
            withContext(Dispatchers.Default) {
                if (!isActive()) {
                    Timber.w("[PoseRepository] Cannot classify - repository not active")
                    return@withContext emptyList()
                }

                Timber.v("[PoseRepository] Delegating classification to data source...")
                when (val result = dataSource.classify(imageData, needMirror)) {
                    is com.po4yka.dancer.data.models.Result.Success -> {
                        Timber.v("[PoseRepository] ✓ Classification successful, got ${result.data.size} predictions")
                        // Convert Map<String, Float> to List<PosePrediction> and sort by probability
                        val predictions =
                            result.data
                                .map { (moveName, probability) ->
                                    PosePrediction(moveName, probability)
                                }
                                .sortedByDescending { it.probability }

                        Timber.v(
                            "[PoseRepository] Top prediction: ${predictions.firstOrNull()?.let { "${it.moveName} (${it.probability})" }}",
                        )
                        predictions
                    }
                    is com.po4yka.dancer.data.models.Result.Error -> {
                        // Don't log errors for model closed - it's expected during shutdown
                        if (result.message.contains("Model closed", ignoreCase = true)) {
                            Timber.v("[PoseRepository] Skipping frame - model is closed")
                        } else {
                            Timber.e(result.exception, "[PoseRepository] ✗ Error classifying pose: ${result.message}")
                        }
                        emptyList()
                    }
                }
            }

        /**
         * Starts the pose classification service.
         *
         * Marks the repository as active and ready for classification.
         * The model and data source are already initialized via dependency injection,
         * so this method simply updates the active state.
         *
         * This operation is thread-safe and idempotent - calling it multiple times
         * is safe and will only log a warning.
         */
        override suspend fun start() {
            withContext(ioDispatcher) {
                Timber.d("[PoseRepository] ========================================")
                Timber.d("[PoseRepository] Start requested")
                synchronized(lock) {
                    if (active) {
                        Timber.w("[PoseRepository] Already started, ignoring request")
                        Timber.d("[PoseRepository] ========================================")
                        return@withContext
                    }

                    Timber.d("[PoseRepository] Setting active flag to true")
                    active = true
                    Timber.d("[PoseRepository] ✓ Repository started successfully")
                    Timber.d("[PoseRepository] ========================================")
                }
            }
        }

        /**
         * Stops the pose classification service.
         *
         * Marks the repository as inactive and releases model resources.
         * After calling this, [start] must be called again before classification can resume.
         *
         * This method properly cleans up TensorFlow Lite model resources including GPU
         * delegates and memory allocations to prevent resource leaks.
         *
         * This operation is thread-safe and idempotent - calling it multiple times
         * is safe and will only log a debug message.
         */
        override suspend fun stop() {
            withContext(ioDispatcher) {
                Timber.d("[PoseRepository] ========================================")
                Timber.d("[PoseRepository] Stop requested")
                synchronized(lock) {
                    if (!active) {
                        Timber.d("[PoseRepository] Already stopped or not started, ignoring request")
                        Timber.d("[PoseRepository] ========================================")
                        return@withContext
                    }

                    Timber.d("[PoseRepository] Setting active flag to false")
                    active = false

                    Timber.d("[PoseRepository] Releasing data source model resources...")
                    // Release model resources (memory, etc.)
                    dataSource.release()

                    Timber.d("[PoseRepository] ✓ Repository stopped successfully")
                    Timber.d("[PoseRepository] ========================================")
                }
            }
        }

        /**
         * Checks if the pose classification service is currently active.
         *
         * @return true if the service is started and ready for classification, false otherwise.
         */
        override fun isActive(): Boolean {
            synchronized(lock) {
                return active
            }
        }
    }
