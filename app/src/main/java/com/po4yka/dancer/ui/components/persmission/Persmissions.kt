package com.po4yka.dancer.ui.components.persmission

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
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
        permissionState.status.isGranted -> {
            content()
        }
        permissionState.status.shouldShowRationale || !permissionState.status.isGranted -> {
            if (doNotShowRationale.value) {
                permissionNotAvailableContent()
            } else {
                PermissionRequestRationale(
                    title = rationaleTitle,
                    iconId = rationaleIconId,
                    description = rationaleDescription,
                    onRequestPermission = { permissionState.launchPermissionRequest() },
                    doNotShowRationale = doNotShowRationale,
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
        doNotShowRationale = doNotShowRationale,
    )
}
