package com.po4yka.dancer.classifier

import android.content.Context
import androidx.camera.core.ImageProxy
import com.po4yka.dancer.ml.DancerBalanced
import com.po4yka.dancer.models.RecognitionModelHelper
import com.po4yka.dancer.utils.ImageExt.flip
import com.po4yka.dancer.utils.ImageExt.toBitmap
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.image.ops.ResizeWithCropOrPadOp
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

        val bitmap = img.toBitmap()
        val workingBitmap = if (needMirror) {
            val cx = bitmap.width / 2f
            val cy = bitmap.height / 2f
            bitmap.flip(-1f, 1f, cx, cy)
        } else {
            bitmap
        }

        val tensorImage = TensorImage(DataType.FLOAT32)
        tensorImage.load(workingBitmap)
        val imageProcessor = ImageProcessor.Builder()
            .add(ResizeOp(IMAGE_NEW_HEIGHT, IMAGE_NET_WIDTH, ResizeOp.ResizeMethod.BILINEAR))
            .add(ResizeWithCropOrPadOp(IMAGE_NEW_HEIGHT, IMAGE_NET_WIDTH))
            .build()
        val processedImage = imageProcessor.process(tensorImage)

        Timber.d("Processed image: width == ${processedImage.width}; height == ${processedImage.height}")

        val tensorBuffer = processedImage.tensorBuffer
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
    }
}
