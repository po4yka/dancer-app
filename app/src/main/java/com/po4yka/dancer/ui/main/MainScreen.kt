package com.po4yka.dancer.ui.main

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.po4yka.dancer.navigation.NavScreen
import com.po4yka.dancer.navigation.createExternalRouter
import com.po4yka.dancer.navigation.navigate
import com.po4yka.dancer.ui.components.bottomnav.BottomBarWithFabDem
import com.po4yka.dancer.ui.theme.DancerTheme
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
                    // Set external roads, mostly for nested navigation.
                    // For example: from Gallery screen to the image info screen.
                    val routeExternalRoad =
                        NavScreen.Gallery.route to createExternalRouter { screen, params ->
                            navController.navigate(screen, params)
                        }
                    BottomBarWithFabDem(
                        modifier = Modifier.navigationBarsPadding(),
                        externalRouters = mapOf(routeExternalRoad)
                    )
                }
            }
        }
    }
}

@ExperimentalPermissionsApi
@ExperimentalCoroutinesApi
@Preview("MainScreen")
@Composable
fun MainScreenPreview() {
    DancerTheme {
        MainScreen()
    }
}
