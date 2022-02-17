package com.po4yka.dancer.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.po4yka.dancer.navigation.NavScreen
import com.po4yka.dancer.navigation.Router
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalPermissionsApi
@ExperimentalCoroutinesApi
@Composable
fun MainScreenNavigation(
    navController: NavHostController,
    externalRouters: Map<String, Router>,
    onBottomBarStateChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {

    NavHost(navController = navController, startDestination = NavScreen.Gallery.route) {
        composable(NavScreen.Gallery.route) {
            LaunchedEffect(Unit) {
                onBottomBarStateChanged.invoke(true)
            }
            GalleryScreen(
                navController = navController,
                router = externalRouters[route],
                modifier = modifier
            )
        }
        composable(NavScreen.Settings.route) {
            LaunchedEffect(Unit) {
                onBottomBarStateChanged.invoke(true)
            }
            SettingsScreen(modifier = modifier)
        }
        composable(NavScreen.Camera.route) {
            LaunchedEffect(Unit) {
                onBottomBarStateChanged.invoke(false)
            }
            CameraScreen(modifier = modifier)
        }
    }
}
