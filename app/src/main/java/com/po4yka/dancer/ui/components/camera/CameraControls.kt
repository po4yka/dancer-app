package com.po4yka.dancer.ui.components.camera

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.po4yka.dancer.R
import com.po4yka.dancer.models.RecognitionState
import com.po4yka.dancer.ui.components.models.ButtonActionRes

@Composable
fun CameraControls(
    recognitionMode: RecognitionState,
    onCaptureClicked: () -> Unit,
    onLensChangeClicked: () -> Unit,
    onRecognitionModeSwitchClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        CameraActionButton(
            onClick = onLensChangeClicked,
            buttonActionRes = ButtonActionRes(
                actionIconId = R.drawable.ic_change_camera,
                actionDescriptionStrId = R.string.change_camera
            ),
            modifier = Modifier
                .size(125.dp)
                .padding(32.dp)
        )
        CapturePictureButton(
            modifier = Modifier
                .size(100.dp)
                .padding(16.dp)
                .align(Alignment.CenterVertically),
            onClick = onCaptureClicked
        )
        CameraActionButton(
            onClick = onRecognitionModeSwitchClicked,
            buttonActionRes = ButtonActionRes(
                actionIconId = if (recognitionMode == RecognitionState.ACTIVE) {
                    R.drawable.ic_eye_open
                } else {
                    R.drawable.ic_eye_close
                },
                actionDescriptionStrId = R.string.recognition_mode
            ),
            modifier = Modifier
                .size(125.dp)
                .padding(32.dp)
        )
    }
}

@Preview
@Composable
fun CameraControlsPreview() {
    Scaffold(
        modifier = Modifier
            .size(125.dp)
            .wrapContentSize()
    ) { contentPadding ->
        CameraControls(
            recognitionMode = RecognitionState.ACTIVE,
            onCaptureClicked = {},
            onLensChangeClicked = {},
            onRecognitionModeSwitchClicked = {},
            modifier = Modifier
                .padding(contentPadding)
                .size(100.dp)
        )
    }
}
