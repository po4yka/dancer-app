package com.po4yka.dancer.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RecognitionModelPredictionResult(
    val name: RecognitionModelName,
    val probability: Float
) : Parcelable {

    companion object {
        fun convertFromModel(modelResults: Map<String, Float>): List<RecognitionModelPredictionResult> {
            return modelResults.map {
                RecognitionModelPredictionResult(
                    RecognitionModelHelper.getClassById(it.key),
                    it.value
                )
            }
        }
    }
}
