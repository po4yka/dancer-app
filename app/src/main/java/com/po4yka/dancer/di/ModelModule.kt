package com.po4yka.dancer.di

import android.content.Context
import com.po4yka.dancer.ml.Dancer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import org.tensorflow.lite.support.model.Model
import timber.log.Timber
import javax.inject.Singleton

/**
 * Model layer Hilt module that provides TensorFlow Lite models and related dependencies.
 *
 * This module is installed in [SingletonComponent], ensuring that expensive model resources
 * are created only once and shared across the application lifecycle.
 *
 * Provides:
 * - TensorFlow Lite Dancer model (pose classification)
 * - Model.Options configured for CPU execution with XNNPACK delegate
 *
 * The model uses XNNPACK delegate which provides excellent performance on ARM64 devices.
 * GPU acceleration has been disabled due to compatibility issues with tensorflow-lite-support.
 *
 * The model is lazily initialized when first requested and properly scoped as a singleton
 * to avoid multiple instances consuming memory.
 */
@Module
@InstallIn(SingletonComponent::class)
object ModelModule {
    /**
     * Provides optimized Model.Options configured for CPU execution.
     *
     * Uses XNNPACK delegate which provides excellent performance on ARM64 devices
     * without the compatibility issues of GPU delegates.
     *
     * @return Configured Model.Options for TensorFlow Lite CPU execution
     */
    @Provides
    @Singleton
    fun provideModelOptions(): Model.Options {
        Timber.d("[ModelModule] Configuring TensorFlow Lite with CPU (XNNPACK delegate)")
        Timber.d("[ModelModule] Thread count: $CPU_THREAD_COUNT")
        return Model.Options.Builder()
            .setNumThreads(CPU_THREAD_COUNT)
            .build()
    }

    /**
     * Provides the TensorFlow Lite Dancer model for pose classification.
     *
     * This is the main ML model used for detecting and classifying dance moves.
     * The model is:
     * - Loaded only once (Singleton scope)
     * - Configured for CPU execution with XNNPACK delegate
     * - Lazily initialized when first requested
     * - Automatically closed when the app is destroyed (handled by Hilt lifecycle)
     *
     * XNNPACK provides highly optimized inference on ARM CPUs, delivering excellent
     * performance without GPU complexity.
     *
     * Note: The model is tied to the application lifecycle. For more granular control
     * (e.g., releasing when not actively classifying), consider injecting this into a
     * repository or use case with proper lifecycle management.
     *
     * @param context Application context for loading the model file
     * @param options Pre-configured model options for CPU execution
     * @return Initialized Dancer TensorFlow Lite model
     */
    @Provides
    @Singleton
    fun provideDancerModel(
        @ApplicationContext context: Context,
        options: Model.Options,
    ): Dancer {
        Timber.d("[ModelModule] ========================================")
        Timber.d("[ModelModule] Initializing Dancer TensorFlow Lite model")
        Timber.d("[ModelModule] Using XNNPACK delegate for CPU acceleration")

        try {
            val model = Dancer.newInstance(context, options)
            Timber.d("[ModelModule] ✓ Model initialized successfully")
            Timber.d("[ModelModule] ========================================")
            return model
        } catch (e: Exception) {
            Timber.e(e, "[ModelModule] ✗ Failed to initialize model")
            Timber.e("[ModelModule] Error: ${e.message}")
            Timber.d("[ModelModule] ========================================")
            throw e
        }
    }

    /**
     * Number of CPU threads to use for model inference.
     * Four threads provide optimal performance on modern mobile processors.
     */
    private const val CPU_THREAD_COUNT = 4
}
