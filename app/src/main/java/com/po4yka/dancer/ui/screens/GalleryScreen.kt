package com.po4yka.dancer.ui.screens

import android.Manifest
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.po4yka.dancer.R
import com.po4yka.dancer.navigation.Router
import com.po4yka.dancer.ui.components.persmission.Permission
import com.po4yka.dancer.ui.components.persmission.PermissionNotAvailable
import com.po4yka.dancer.ui.theme.DancerTheme
import com.po4yka.gallerypicker.ui.GalleryPicker

@OptIn(
    ExperimentalFoundationApi::class,
    ExperimentalPermissionsApi::class
)
@Composable
fun GalleryScreen(
    @Suppress("UNUSED_PARAMETER") navController: NavController,
    @Suppress("UNUSED_PARAMETER") router: Router?,
    modifier: Modifier = Modifier
) {
    Permission(
        permission = Manifest.permission.READ_EXTERNAL_STORAGE,
        rationaleTitle = stringResource(id = R.string.show_gallery_content),
        rationaleIconId = R.drawable.ic_gallery,
        rationaleDescription = stringResource(id = R.string.gallery_permission_request_text),
        permissionNotAvailableContent = {
            PermissionNotAvailable(unavailableExplanationResId = R.string.can_not_work_with_no_gallery_access)
        }
    ) {
        GalleryPicker(modifier = modifier)
    }
}

@Preview("default")
@Preview("large font", fontScale = 2f)
@ExperimentalFoundationApi
@Composable
fun GalleryScreenPreview() {
    DancerTheme {
        val navController = rememberNavController()
        GalleryScreen(navController, null)
    }
}
