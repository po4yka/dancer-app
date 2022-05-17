package com.po4yka.dancer.ui.components.camera

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.po4yka.dancer.R
import com.po4yka.dancer.ui.components.models.ButtonActionRes

@Composable
fun CameraActionButton(
    onClick: () -> Unit,
    buttonActionRes: ButtonActionRes,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter,
    ) {
        OutlinedButton(
            onClick = onClick,
            modifier = Modifier.size(50.dp),
            border = BorderStroke(0.dp, Color.Transparent),
            shape = CircleShape,
            colors =
                ButtonDefaults.buttonColors(
                    backgroundColor = Color.Transparent,
                    contentColor = Color.White,
                ),
            contentPadding = PaddingValues(0.dp),
        ) {
            Icon(
                painter = painterResource(id = buttonActionRes.actionIconId),
                contentDescription = stringResource(buttonActionRes.actionDescriptionStrId),
                modifier = Modifier.size(30.dp),
            )
        }
    }
}

@Preview
@Composable
fun CameraActionButtonPreview() {
    Scaffold(
        modifier =
            Modifier
                .size(125.dp)
                .wrapContentSize(),
    ) { contentPadding ->
        CameraActionButton(
            onClick = {},
            buttonActionRes =
                ButtonActionRes(
                    actionIconId = R.drawable.ic_change_camera,
                    actionDescriptionStrId = R.string.change_camera,
                ),
            modifier =
                Modifier
                    .padding(contentPadding)
                    .size(100.dp),
        )
    }
}
