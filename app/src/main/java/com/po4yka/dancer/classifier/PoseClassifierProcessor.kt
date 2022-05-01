package com.po4yka.dancer.classifier

import android.content.Context
import androidx.camera.core.ImageProxy
import com.po4yka.dancer.ml.Dancer
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
    private val context: Context
) {

    private var model: Dancer? = null

    @androidx.camera.core.ExperimentalGetImage
    fun classify(
        imageProxy: ImageProxy,
        needMirror: Boolean = false
    ): Map<String, Float> {

        model = if (model == null) Dancer.newInstance(context) else model

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

        val tensorImage = TensorImage(DataType.FLOAT32)
        tensorImage.load(workingBitmap)

        val buffBeforeNormalize = tensorImage
            .tensorBuffer
            .floatArray
            .slice(TEST_BUFFER_SLICE)
            .joinToString(", ")
        Timber.d("tensorBuffer before normalize: $buffBeforeNormalize \u2026")

        val imageProcessor = ImageProcessor.Builder()
            .add(ResizeOp(IMAGE_NEW_HEIGHT, IMAGE_NET_WIDTH, ResizeOp.ResizeMethod.BILINEAR))
            .add(NormalizeOp(IMAGE_MEAN, IMAGE_STD))
            .build()
        val processedImage = imageProcessor.process(tensorImage)
        val tensorBuffer = processedImage.tensorBuffer

        val normalizedBuff = processedImage
            .tensorBuffer
            .floatArray
            .slice(TEST_BUFFER_SLICE)
            .joinToString(", ")
        Timber.d("tensorBuffer after normalize: $normalizedBuff \u2026")

        Timber.d(
            "Processed image: width == ${processedImage.width}; " +
                "height == ${processedImage.height}"
        )

        val outputs = model?.process(tensorBuffer) ?: return emptyMap()
        val outputFeatures = outputs.outputFeature0AsTensorBuffer
        val tensorLabel =
            TensorLabel(RecognitionModelHelper.getClassesIds(), outputFeatures)

        val probabilities = tensorLabel.mapWithFloatValue

        Timber.d("Output probabilities: $probabilities")

        return probabilities
    }

    fun stop() {
        model?.close()
        model = null
    }

    companion object {
        const val IMAGE_NET_WIDTH = 160
        const val IMAGE_NEW_HEIGHT = 256

        // normalize the input from 0 to 1
        const val IMAGE_MEAN = 0.0f
        const val IMAGE_STD = 255.0f

        private val TEST_BUFFER_SLICE = 1..10
    }
}
