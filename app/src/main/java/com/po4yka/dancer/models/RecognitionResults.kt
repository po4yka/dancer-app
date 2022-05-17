package com.po4yka.dancer.models

data class RecognitionResults(
    val isDetected: Boolean,
    val results: List<RecognitionModelPredictionResult>,
)
