package com.po4yka.dancer.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.po4yka.dancer.ui.components.BottomBarWithFabDem
import com.po4yka.dancer.ui.theme.DancerTheme

@ExperimentalPermissionsApi
@Composable
fun DancerApp() {
    ProvideWindowInsets {
        DancerTheme {
            val systemUiController = rememberSystemUiController()

            SideEffect {
                systemUiController.setSystemBarsColor(
                    Color.Transparent,
                    darkIcons = false
                )
            }

            val navController = rememberNavController()
            BottomBarWithFabDem(navController)
        }
    }
}
