package com.po4yka.dancer.ui.navigation

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.po4yka.dancer.R
import com.po4yka.dancer.ui.components.Permission
import com.po4yka.dancer.ui.components.PermissionNotAvailable

@ExperimentalPermissionsApi
@Composable
fun CameraScreen() {
    Permission(
        permission = android.Manifest.permission.CAMERA,
        rationaleTitle = stringResource(id = R.string.recognize_from_camera),
        rationaleIconId = R.drawable.ic_camera_light,
        rationaleDescription = stringResource(id = R.string.camera_permission_request_text),
        permissionNotAvailableContent = {
            PermissionNotAvailable()
        }
    ) {
        Text(stringResource(id = R.string.permission_granted))
    }
}
