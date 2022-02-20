package com.po4yka.dancer.ui.components

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionRequired
import com.google.accompanist.permissions.rememberPermissionState
import com.po4yka.dancer.R
import com.po4yka.dancer.ui.screens.CustomPermissionDialog

@ExperimentalPermissionsApi
@Composable
fun Permission(
    permission: String,
    rationaleTitle: String,
    rationaleIconId: Int = R.drawable.ic_settings_change_light,
    rationaleDescription: String = stringResource(R.string.important_permission),
    permissionNotAvailableContent: @Composable () -> Unit = { },
    content: @Composable () -> Unit = { },
) {

    val doNotShowRationale = remember { mutableStateOf(false) }

    val permissionState = rememberPermissionState(permission)

    when {
        permissionState.hasPermission -> {
            content()
        }
        permissionState.shouldShowRationale || !permissionState.permissionRequested -> {
            if (doNotShowRationale.value) {
                permissionNotAvailableContent()
            } else {
                PermissionRequired(
                    permissionState = permissionState,
                    permissionNotGrantedContent = {
                        if (doNotShowRationale.value) {
                            Text(stringResource(id = R.string.feature_not_available))
                        } else {
                            PermissionRequestRationale(
                                title = rationaleTitle,
                                iconId = rationaleIconId,
                                description = rationaleDescription,
                                onRequestPermission = { permissionState.launchPermissionRequest() },
                                doNotShowRationale = doNotShowRationale
                            )
                        }
                    },
                    permissionNotAvailableContent = permissionNotAvailableContent,
                    content = content
                )
            }
        }
        // If the criteria above hasn't been met, the user denied the permission
        else -> {
            permissionNotAvailableContent()
        }
    }
}

@Composable
private fun PermissionRequestRationale(
    title: String,
    iconId: Int,
    description: String,
    onRequestPermission: () -> Unit,
    doNotShowRationale: MutableState<Boolean>,
) {
    CustomPermissionDialog(
        title = title,
        iconId = iconId,
        description = description,
        allowAction = onRequestPermission,
        doNotShowRationale = doNotShowRationale
    )
}

@Composable
fun PermissionNotAvailable(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .fillMaxSize()
            .wrapContentSize()
    ) {
        Text(
            stringResource(id = R.string.can_not_work_with_no_camera),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = {
            context.startActivity(
                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", context.packageName, null)
                }
            )
        }) {
            Text(stringResource(id = R.string.open_settings))
        }
    }
}
