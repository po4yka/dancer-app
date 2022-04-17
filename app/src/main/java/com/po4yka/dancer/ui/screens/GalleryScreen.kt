package com.po4yka.dancer.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.po4yka.dancer.navigation.Router
import com.po4yka.dancer.ui.theme.DancerTheme
import com.po4yka.gallerypicker.ui.GalleryPicker

@Composable
@ExperimentalFoundationApi
fun GalleryScreen(
    @Suppress("UNUSED_PARAMETER") navController: NavController,
    @Suppress("UNUSED_PARAMETER") router: Router?,
    modifier: Modifier = Modifier
) {
    GalleryPicker(
        modifier = modifier,
        onPhotoSelected = { }
    )
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
