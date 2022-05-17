package com.po4yka.dancer.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.po4yka.dancer.R
import com.po4yka.dancer.ui.theme.BlackAlpha24
import com.po4yka.dancer.ui.theme.BlueA400
import com.po4yka.dancer.ui.theme.BlueA500
import com.po4yka.dancer.ui.theme.SteelGray200
import com.po4yka.dancer.ui.theme.WhiteBlue20

@Composable
fun CustomPermissionDialog(
    iconId: Int,
    title: String,
    description: String,
    allowAction: () -> Unit,
    doNotShowRationale: MutableState<Boolean>,
) {
    Dialog(
        onDismissRequest = { doNotShowRationale.value = true },
    ) {
        CustomPermissionDialogUI(
            iconId = iconId,
            title = title,
            description = description,
            allowAction = allowAction,
            doNotShowRationale = doNotShowRationale,
        )
    }
}

@Composable
fun CustomPermissionDialogUI(
    modifier: Modifier = Modifier,
    iconId: Int,
    title: String,
    description: String,
    allowAction: () -> Unit,
    doNotShowRationale: MutableState<Boolean>,
) {
    Card(
        shape = RoundedCornerShape(15.dp),
        modifier = Modifier.padding(10.dp, 5.dp, 10.dp, 10.dp),
        elevation = 8.dp,
    ) {
        Column(
            modifier.background(Color.White),
        ) {
            Image(
                painter = painterResource(id = iconId),
                contentDescription = null, // decorative
                contentScale = ContentScale.Fit,
                colorFilter =
                    ColorFilter.tint(
                        color = BlueA500,
                    ),
                modifier =
                    Modifier
                        .padding(top = 25.dp)
                        .height(75.dp)
                        .fillMaxWidth(),
            )

            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = title,
                    textAlign = TextAlign.Center,
                    color = Color.Black,
                    modifier =
                        Modifier
                            .padding(top = 5.dp)
                            .fillMaxWidth(),
                    style = MaterialTheme.typography.h5,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = description,
                    textAlign = TextAlign.Center,
                    color = Color.Black,
                    modifier =
                        Modifier
                            .padding(top = 10.dp, start = 25.dp, end = 25.dp)
                            .fillMaxWidth(),
                    style = MaterialTheme.typography.body2,
                )
            }

            CustomPermissionDialogBtn(allowAction, doNotShowRationale)
        }
    }
}

@Composable
private fun CustomPermissionDialogBtn(
    allowAction: () -> Unit,
    doNotShowRationale: MutableState<Boolean>,
) {
    Row(
        Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Max)
            .padding(top = 10.dp)
            .background(BlueA400),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        TextButton(
            onClick = {
                doNotShowRationale.value = true
            },
        ) {
            Text(
                text = stringResource(id = R.string.not_now),
                fontWeight = FontWeight.Bold,
                color = SteelGray200,
                textAlign = TextAlign.Center,
                modifier =
                    Modifier
                        .padding(top = 5.dp, bottom = 5.dp)
                        .align(Alignment.CenterVertically),
            )
        }
        Divider(
            modifier =
                Modifier
                    .fillMaxHeight()
                    .width(1.dp),
            color = BlackAlpha24,
        )
        TextButton(
            onClick = {
                doNotShowRationale.value = true
                allowAction.invoke()
            },
        ) {
            Text(
                text = stringResource(id = R.string.allow),
                fontWeight = FontWeight.ExtraBold,
                color = WhiteBlue20,
                textAlign = TextAlign.Center,
                modifier =
                    Modifier
                        .padding(top = 5.dp, bottom = 5.dp)
                        .align(Alignment.CenterVertically),
            )
        }
    }
}

@SuppressLint("UnrememberedMutableState")
@Preview(name = "Custom Dialog")
@Composable
fun MyDialogUIPreview() {
    CustomPermissionDialogUI(
        iconId = R.drawable.ic_heart_light,
        title = "Receive Corgi",
        description = "Allow this very-very important permission for happiness of puppies.",
        allowAction = {},
        doNotShowRationale = mutableStateOf(false),
    )
}
