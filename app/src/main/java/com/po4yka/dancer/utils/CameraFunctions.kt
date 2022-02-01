package com.po4yka.dancer.utils

import androidx.camera.core.CameraSelector
import com.po4yka.dancer.models.RecognitionState

fun switchLens(lens: Int) = if (CameraSelector.LENS_FACING_FRONT == lens) {
    CameraSelector.LENS_FACING_BACK
} else {
    CameraSelector.LENS_FACING_FRONT
}

fun switchRecognitionMode(state: RecognitionState): RecognitionState =
    if (state == RecognitionState.ACTIVE) {
        RecognitionState.DISABLE
    } else {
        RecognitionState.ACTIVE
    }
