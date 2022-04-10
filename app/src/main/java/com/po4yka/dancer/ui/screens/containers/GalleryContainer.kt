package com.po4yka.dancer.ui.screens.containers

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import com.po4yka.dancer.models.ImageModel
import com.po4yka.dancer.navigation.NavScreen
import com.po4yka.dancer.navigation.NavigationController
import com.po4yka.dancer.navigation.Router
import com.po4yka.dancer.ui.screens.GalleryScreen
import com.po4yka.dancer.ui.screens.ImageScreen
import kotlinx.coroutines.ExperimentalCoroutinesApi

@Composable
@androidx.camera.core.ExperimentalGetImage
@OptIn(
    ExperimentalFoundationApi::class,
    ExperimentalCoroutinesApi::class,
)
fun GalleryContainer(
    externalRouter: Router,
) {
    NavigationController(
        startDestination = NavScreen.Gallery.route,
        router = externalRouter,
        screens = listOf(
            Pair(NavScreen.Gallery.route) { nav, router, _ -> GalleryScreen(nav, router) },
            Pair(NavScreen.ImageInfo.route) { _, _, params ->
                params?.getParcelable<ImageModel>(ImageModel.IMG)?.let {
                    ImageScreen(model = it)
                }
            }
        )
    )
}
