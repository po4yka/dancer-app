package com.po4yka.dancer.classifier

import android.content.Context
import androidx.camera.core.ImageProxy
import com.po4yka.dancer.ml.DancerBalanced
import com.po4yka.dancer.models.RecognitionModelHelper
import com.po4yka.dancer.utils.ImageExt.flip
import com.po4yka.dancer.utils.ImageExt.rotate
import com.po4yka.dancer.utils.ImageExt.toBitmap
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.label.TensorLabel
import timber.log.Timber

class PoseClassifierProcessor(
    context: Context
) {

    private val model = DancerBalanced.newInstance(context)

    @androidx.camera.core.ExperimentalGetImage
    fun classify(
        imageProxy: ImageProxy,
        needMirror: Boolean = false
    ): Map<String, Float> {
        val img = imageProxy.image ?: return emptyMap()

        Timber.d("Analyze image: width == ${img.width}; height == ${img.height}")

        val rotationDegree = imageProxy.imageInfo.rotationDegrees.toFloat()
        val bitmap = img.toBitmap()?.rotate(rotationDegree) ?: return emptyMap()

        val workingBitmap = if (needMirror) {
            val cx = bitmap.width / 2f
            val cy = bitmap.height / 2f
            bitmap.flip(-1f, 1f, cx, cy)
        } else {
            bitmap
        }

        // --- TODO: remove this ---

//        if (SAVE_IMAGES_COUNT_BEFORE > 0) {
//            saveMediaToStorage(context, workingBitmap, "before_process_$SAVE_IMAGES_COUNT_BEFORE")
//            SAVE_IMAGES_COUNT_BEFORE--
//        }

        // ----------------------------

        val tensorImage = TensorImage(DataType.FLOAT32)
        tensorImage.load(workingBitmap)

        Timber.d(
            "tensorBuffer before normalize: ${
            tensorImage.tensorBuffer.floatArray.slice(1..10).joinToString(", ")
            } \u2026"
        )

        val imageProcessor = ImageProcessor.Builder()
            .add(ResizeOp(IMAGE_NEW_HEIGHT, IMAGE_NET_WIDTH, ResizeOp.ResizeMethod.BILINEAR))
            .add(NormalizeOp(IMAGE_MEAN, IMAGE_STD))
            .build()
        val processedImage = imageProcessor.process(tensorImage)
        val tensorBuffer = processedImage.tensorBuffer

        // --- TODO: remove this ---

//        val checkImageProcessor = ImageProcessor.Builder()
//            .add(NormalizeOp(0.0f, 1 / 255.0f))
//            .build()
//
//        val checkProcessedImage = checkImageProcessor.process(processedImage)
//
//        // SAVE BITMAP AFTER PROCESS
//        if (SAVE_IMAGES_COUNT_AFTER > 0) {
//            saveMediaToStorage(
//                context,
//                checkProcessedImage.bitmap,
//                "after_process_$SAVE_IMAGES_COUNT_AFTER"
//            )
//            SAVE_IMAGES_COUNT_AFTER--
//        }

        // --------------------------

        Timber.d(
            "tensorBuffer after normalize: ${
            processedImage.tensorBuffer.floatArray.slice(1..10).joinToString(", ")
            } \u2026"
        )

        Timber.d("Processed image: width == ${processedImage.width}; height == ${processedImage.height}")

        val outputs = model.process(tensorBuffer)
        val outputFeatures = outputs.outputFeature0AsTensorBuffer
        val tensorLabel =
            TensorLabel(RecognitionModelHelper.getClassesIds(), outputFeatures)

        val probabilities = tensorLabel.mapWithFloatValue

        Timber.d("Output probabilities: $probabilities")

        return probabilities
    }

    fun stop() {
        model.close()
    }

    companion object {
        const val IMAGE_NET_WIDTH = 160
        const val IMAGE_NEW_HEIGHT = 256

        // normalize the input from 0 to 1
        const val IMAGE_MEAN = 0.0f
        const val IMAGE_STD = 255.0f

        // --- TODO: remove this ---
//        var SAVE_IMAGES_COUNT_BEFORE = 10
//        var SAVE_IMAGES_COUNT_AFTER = 10
    }
}
