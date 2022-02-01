package com.po4yka.dancer.classifier

import android.content.Context
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.po4yka.dancer.models.RecognitionModelPredictionResult
import com.po4yka.dancer.models.RecognitionResults

@androidx.camera.core.ExperimentalGetImage
class MoveAnalyzer(
    private val context: Context,
    private val onMovementsClassified: (RecognitionResults) -> Unit
) : ImageAnalysis.Analyzer {

    val isActive: Boolean
        get() = classifier != null

    private var classifier: PoseClassifierProcessor? = null

    override fun analyze(imageProxy: ImageProxy) {
        val classificationResultsFromModel = classifier?.classify(imageProxy)
        val classificationRes = if (classificationResultsFromModel != null) {
            RecognitionModelPredictionResult.convertFromModel(classificationResultsFromModel)
        } else {
            emptyList()
        }.sortedByDescending { it.probability }

        onMovementsClassified.invoke(
            RecognitionResults(
                isDetected = (classificationRes.firstOrNull()?.probability ?: 0f) > THRESHOLD_VALUE,
                results = classificationRes
            )
        )

        imageProxy.close()
    }

    fun start() {
        classifier = PoseClassifierProcessor(context)
    }

    fun stop() {
        classifier?.stop()
        classifier = null
    }

    private companion object {
        const val THRESHOLD_VALUE = 2.5f
    }
}
