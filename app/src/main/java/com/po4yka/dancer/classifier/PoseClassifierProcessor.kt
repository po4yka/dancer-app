package com.po4yka.dancer.classifier

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Rect
import android.graphics.YuvImage
import android.media.Image
import androidx.camera.core.ImageProxy
import com.po4yka.dancer.ml.DancerBalanced
import com.po4yka.dancer.models.RecognitionModelHelper
import java.io.ByteArrayOutputStream
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.image.ops.ResizeWithCropOrPadOp
import org.tensorflow.lite.support.label.TensorLabel
import timber.log.Timber

class PoseClassifierProcessor(
    private val context: Context
) {

    private val model = DancerBalanced.newInstance(context)

    @androidx.camera.core.ExperimentalGetImage
    fun classify(imageProxy: ImageProxy) {
        val img = imageProxy.image ?: return

        Timber.d("Analyze image: width == ${img.width}; height == ${img.height}")

        val bitmap = img.toBitmap()
        val tensorImage = TensorImage(DataType.FLOAT32)
        tensorImage.load(bitmap)

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
            TensorLabel(RecognitionModelHelper.getClassesNames(context), outputFeatures)

        val probabilities = tensorLabel.mapWithFloatValue

        Timber.d("Output probabilities: $probabilities")
    }

    fun stop() {
        model.close()
    }

    private fun Image.toBitmap(): Bitmap {
        val yBuffer = planes[0].buffer // Y
        val vuBuffer = planes[2].buffer // VU

        val ySize = yBuffer.remaining()
        val vuSize = vuBuffer.remaining()

        val nv21 = ByteArray(ySize + vuSize)

        yBuffer.get(nv21, 0, ySize)
        vuBuffer.get(nv21, ySize, vuSize)

        val yuvImage = YuvImage(nv21, ImageFormat.NV21, this.width, this.height, null)
        val out = ByteArrayOutputStream()
        yuvImage.compressToJpeg(Rect(0, 0, yuvImage.width, yuvImage.height), 50, out)
        val imageBytes = out.toByteArray()
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    }

    companion object {
        const val IMAGE_NET_WIDTH = 160
        const val IMAGE_NEW_HEIGHT = 256
    }
}
