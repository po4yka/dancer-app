package com.po4yka.dancer.classifier

import android.content.Context
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy

@androidx.camera.core.ExperimentalGetImage
class MoveAnalyzer(context: Context) : ImageAnalysis.Analyzer {

    private val classifier = PoseClassifierProcessor(context)

    override fun analyze(image: ImageProxy) {
        classifier.classify(image)
    }

    fun stop() {
        classifier.stop()
    }
}
