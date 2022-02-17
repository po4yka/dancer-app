package com.po4yka.dancer.navigation

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

private typealias ScreenNav = Pair<String, @Composable (NavController, Router?, Bundle?) -> Unit>

@Composable
fun NavigationController(
    router: Router? = null,
    startDestination: String,
    screens: List<ScreenNav> = emptyList(),
) {
    val navigation = rememberNavController()

    NavHost(navController = navigation, startDestination = startDestination) {
        screens.forEach { screen ->
            composable(screen.first) {
                screen.second.invoke(
                    navigation,
                    router,
                    navigation.previousBackStackEntry?.arguments
                )
            }
        }
    }
}
