package com.po4yka.dancer.ui.main

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.po4yka.dancer.navigation.NavScreen
import com.po4yka.dancer.navigation.createExternalRouter
import com.po4yka.dancer.navigation.navigate
import com.po4yka.dancer.ui.components.bottom_navigation.BottomBarWithFabDem
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalPermissionsApi
@ExperimentalCoroutinesApi
@Composable
fun MainScreen() {

    ProvideWindowInsets {
        Surface(color = MaterialTheme.colors.background) {
            val navController = rememberNavController()

            NavHost(navController = navController, startDestination = NavScreen.Main.route) {
                composable(NavScreen.Main.route) {
                    val routeExternalRoad =
                        NavScreen.Gallery.route to createExternalRouter { screen, params ->
                            navController.navigate(screen, params)
                        }
                    BottomBarWithFabDem(mapOf(routeExternalRoad))
                }
            }
        }
    }
}
