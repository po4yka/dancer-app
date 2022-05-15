package com.po4yka.dancer.data.datasource

import android.graphics.Bitmap
import com.po4yka.dancer.data.models.Result
import com.po4yka.dancer.data.models.runCatchingAsResult
import com.po4yka.dancer.domain.model.ImageData
import com.po4yka.dancer.ml.Dancer
import com.po4yka.dancer.models.RecognitionModelHelper
import com.po4yka.dancer.utils.ImageExt.flip
import com.po4yka.dancer.utils.ImageExt.rotate
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import timber.log.Timber
import java.nio.ByteBuffer

/**
 * Data source responsible for pose classification using TensorFlow Lite model.
 *
 * This class handles the core TensorFlow Lite operations for pose recognition, including:
 * - Image preprocessing (rotation, mirroring, resizing, normalization)
 * - Model inference
 * - Output processing
 *
 * The data source is designed to be testable by accepting the model as a parameter,
 * allowing for dependency injection and easier unit testing.
 *
 * Thread Safety:
 * - Model access is synchronized to prevent use-after-close errors
 * - Closed flag is volatile to ensure visibility across threads
 *
 * @property model The TensorFlow Lite model instance used for pose classification
 */
class PoseClassifierDataSource(
    private val model: Dancer,
) {
    /**
     * Flag indicating whether the model has been closed.
     * Volatile ensures visibility across threads.
     */
    @Volatile
    private var isClosed = false

    /**
     * Lock for synchronizing model access during classification and release.
     */
    private val modelLock = Any()

    /**
     * Classifies the pose in the provided image.
     *
     * This method performs the following steps:
     * 1. Converts ImageData to Bitmap
     * 2. Rotates the image based on device orientation
     * 3. Optionally mirrors the image (for front-facing camera)
     * 4. Converts to TensorImage and preprocesses (resize, normalize)
     * 5. Runs model inference
     * 6. Processes output probabilities
     *
     * @param imageData The image data to analyze
     * @param needMirror Whether to mirror the image horizontally (typically true for front camera)
     * @return Result containing a map of pose class IDs to their confidence probabilities,
     *         or an Error if classification fails
     */
    fun classify(
        imageData: ImageData,
        needMirror: Boolean = false,
    ): Result<Map<String, Float>> {
        // Check if model is closed before proceeding - return error directly to avoid exception
        if (isClosed) {
            Timber.d("[PoseClassifier] Model is closed, skipping frame")
            return Result.Error(
                IllegalStateException("Model closed"),
                "Model closed",
            )
        }

        return runCatchingAsResult {
            val startTime = System.currentTimeMillis()
            Timber.d("[PoseClassifier] ========================================")
            Timber.d("[PoseClassifier] Starting classification")
            Timber.d("[PoseClassifier] Image: ${imageData.width}x${imageData.height}, rotation=${imageData.rotation}, mirror=$needMirror")

            // Convert ImageData to Bitmap
            val rotationDegree = imageData.rotation.toFloat()
            val bitmap =
                imageDataToBitmap(imageData)?.rotate(rotationDegree)
                    ?: throw IllegalStateException("Failed to convert image data to bitmap or rotate")

            // Apply mirroring if needed (typically for front-facing camera)
            val workingBitmap =
                if (needMirror) {
                    val cx = bitmap.width / 2f
                    val cy = bitmap.height / 2f
                    bitmap.flip(-1f, 1f, cx, cy)
                } else {
                    bitmap
                }

            // Convert bitmap to TensorImage
            val tensorImage = TensorImage(DataType.FLOAT32)
            tensorImage.load(workingBitmap)

            // Log tensor buffer before normalization (for debugging)
            if (Timber.treeCount > 0) {
                val buffBeforeNormalize =
                    tensorImage
                        .tensorBuffer
                        .floatArray
                        .slice(TEST_BUFFER_SLICE)
                        .joinToString(", ")
                Timber.d("tensorBuffer before normalize: $buffBeforeNormalize …")
            }

            // Create image processor for preprocessing
            val imageProcessor =
                ImageProcessor.Builder()
                    .add(ResizeOp(IMAGE_NEW_HEIGHT, IMAGE_NET_WIDTH, ResizeOp.ResizeMethod.BILINEAR))
                    .add(NormalizeOp(IMAGE_MEAN, IMAGE_STD))
                    .build()

            // Process the image
            val processedImage = imageProcessor.process(tensorImage)
            val tensorBuffer = processedImage.tensorBuffer

            // Log tensor buffer after normalization (for debugging)
            if (Timber.treeCount > 0) {
                val normalizedBuff =
                    processedImage
                        .tensorBuffer
                        .floatArray
                        .slice(TEST_BUFFER_SLICE)
                        .joinToString(", ")
                Timber.d("tensorBuffer after normalize: $normalizedBuff …")
                Timber.d(
                    "Processed image: width == ${processedImage.width}; " +
                        "height == ${processedImage.height}",
                )
            }

            // Synchronize model access to prevent use-after-close errors
            Timber.d("[PoseClassifier] Acquiring model lock for inference...")
            val inferenceStartTime = System.currentTimeMillis()
            val (outputs, outputFeatures) =
                synchronized(modelLock) {
                    // Double-check after acquiring lock
                    if (isClosed) {
                        Timber.w("[PoseClassifier] Model closed during classification")
                        throw IllegalStateException("Model has been closed")
                    }

                    Timber.d("[PoseClassifier] Running model inference...")
                    // Run model inference
                    val modelOutputs = model.process(tensorBuffer)
                    val features = modelOutputs.outputFeature0AsTensorBuffer
                    Pair(modelOutputs, features)
                }
            val inferenceTime = System.currentTimeMillis() - inferenceStartTime
            Timber.d("[PoseClassifier] Inference completed in ${inferenceTime}ms")

            // Get raw logits from the model
            val logits = outputFeatures.floatArray
            Timber.d("[PoseClassifier] Got ${logits.size} raw logits from model")

            // Apply softmax to convert logits to probabilities
            Timber.d("[PoseClassifier] Applying softmax to convert logits to probabilities...")
            val probabilities = softmax(logits)

            // Convert to labeled map
            val classIds = RecognitionModelHelper.getClassesIds()
            val result = classIds.zip(probabilities.toList()).toMap()

            val totalTime = System.currentTimeMillis() - startTime
            Timber.d("[PoseClassifier] ✓ Classification completed in ${totalTime}ms")
            Timber.d("[PoseClassifier] Top predictions: ${result.entries.sortedByDescending { it.value }.take(3)}")
            Timber.d("[PoseClassifier] ========================================")

            result
        }
    }

    /**
     * Converts ImageData to Bitmap.
     *
     * This method handles the conversion from domain ImageData model to Android Bitmap.
     * Currently supports RGBA_8888 format from the first plane.
     *
     * @param imageData The image data to convert
     * @return Bitmap representation of the image, or null if conversion fails
     */
    private fun imageDataToBitmap(imageData: ImageData): Bitmap? {
        return try {
            if (imageData.planes.isEmpty()) {
                Timber.w("ImageData has no planes")
                return null
            }

            val plane = imageData.planes[0]
            val buffer = ByteBuffer.wrap(plane.buffer)
            val pixelStride = plane.pixelStride
            val rowStride = plane.rowStride
            val rowPadding = rowStride - pixelStride * imageData.width

            val bitmap =
                Bitmap.createBitmap(
                    imageData.width + rowPadding / pixelStride,
                    imageData.height,
                    Bitmap.Config.ARGB_8888,
                )
            bitmap.copyPixelsFromBuffer(buffer)
            bitmap
        } catch (e: Exception) {
            Timber.e(e, "Failed to convert ImageData to Bitmap")
            null
        }
    }

    /**
     * Applies the softmax function to convert logits to probabilities.
     *
     * The softmax function converts a vector of raw scores (logits) into probabilities
     * that sum to 1.0. This is necessary because TensorFlow Lite models often output
     * raw logits (which can be negative) rather than normalized probabilities.
     *
     * Formula: softmax(x_i) = exp(x_i) / sum(exp(x_j)) for all j
     *
     * @param logits Array of raw model output scores (can be negative)
     * @return Array of probabilities in range [0.0, 1.0] that sum to 1.0
     */
    private fun softmax(logits: FloatArray): FloatArray {
        // Find max value for numerical stability (prevents overflow in exp)
        val maxLogit = logits.maxOrNull() ?: 0f

        // Calculate exp(x - max) for each logit
        val exps = logits.map { kotlin.math.exp((it - maxLogit).toDouble()).toFloat() }

        // Calculate sum of all exponentials
        val sumExps = exps.sum()

        // Normalize to get probabilities
        return exps.map { it / sumExps }.toFloatArray()
    }

    /**
     * Releases resources held by the model.
     *
     * This method should be called when the data source is no longer needed
     * to free up memory and other resources used by the TensorFlow Lite model.
     *
     * Thread-safe: Synchronizes access and sets the closed flag to prevent
     * further model operations after release.
     */
    fun release() {
        Timber.d("[PoseClassifier] ========================================")
        Timber.d("[PoseClassifier] Releasing model resources...")
        synchronized(modelLock) {
            if (isClosed) {
                Timber.d("[PoseClassifier] Model already closed, skipping release")
                Timber.d("[PoseClassifier] ========================================")
                return
            }

            Timber.d("[PoseClassifier] Setting closed flag to prevent new frames")
            isClosed = true

            try {
                Timber.d("[PoseClassifier] Closing TensorFlow Lite model...")
                model.close()
                Timber.d("[PoseClassifier] ✓ Model released successfully")
            } catch (e: Exception) {
                Timber.e(e, "[PoseClassifier] ✗ Error releasing model: ${e.message}")
            }
        }
        Timber.d("[PoseClassifier] ========================================")
    }

    companion object {
        /**
         * Target width for the input image after resizing.
         * This matches the input dimensions expected by the model.
         */
        const val IMAGE_NET_WIDTH = 160

        /**
         * Target height for the input image after resizing.
         * This matches the input dimensions expected by the model.
         */
        const val IMAGE_NEW_HEIGHT = 256

        /**
         * Mean value for image normalization.
         * Used to normalize pixel values from [0, 255] to [0, 1].
         */
        const val IMAGE_MEAN = 0.0f

        /**
         * Standard deviation for image normalization.
         * Used to normalize pixel values from [0, 255] to [0, 1].
         */
        const val IMAGE_STD = 255.0f

        /**
         * Range of tensor buffer indices to log for debugging purposes.
         */
        private val TEST_BUFFER_SLICE = 1..10
    }
}
