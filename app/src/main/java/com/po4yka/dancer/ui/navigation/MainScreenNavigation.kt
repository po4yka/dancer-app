package com.po4yka.dancer.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.po4yka.dancer.ui.GalleryScreen
import com.po4yka.dancer.ui.SettingsScreen

@Composable
fun MainScreenNavigation(navController: NavHostController) {

    NavHost(navController, startDestination = Screen.Gallery.route) {

        composable(Screen.Gallery.route) {
            GalleryScreen()
        }
        composable(Screen.Settings.route) {
            SettingsScreen()
        }
        composable(Screen.Camera.route) {
            // CameraScreen()
        }
    }
}
