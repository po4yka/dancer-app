package com.po4yka.dancer.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.po4yka.dancer.ui.root.RootViewModel
import com.po4yka.dancer.ui.theme.DancerTheme
import com.po4yka.dancer.ui.theme.SteelGray500
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
@androidx.camera.core.ExperimentalGetImage
@ExperimentalPermissionsApi
class MainActivity : ComponentActivity() {

    @VisibleForTesting
    internal val viewModel: RootViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Turn off the decor fitting system windows, which means we need to through handling insets
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            CompositionLocalProvider {
                ProvideWindowInsets {
                    DancerTheme {
                        val systemUiController = rememberSystemUiController()
                        val useDarkIcons = MaterialTheme.colors.isLight
                        // TODO: change to `MaterialTheme.colors.primaryVariant` after theming setup
                        val originalNavBarColor = SteelGray500
                        val setDefaultNavBarColor = {
                            systemUiController.setNavigationBarColor(
                                color = originalNavBarColor,
                                darkIcons = useDarkIcons
                            )
                        }
                        val setCustomNavBarColor =
                            { newColor: Color?, forcedUseDarkIcons: Boolean? ->
                                systemUiController.setNavigationBarColor(
                                    color = newColor ?: originalNavBarColor,
                                    darkIcons = forcedUseDarkIcons ?: useDarkIcons
                                )
                            }
                        val setCustomStatusBarColor =
                            { newColor: Color?, forcedUseDarkIcons: Boolean? ->
                                systemUiController.setStatusBarColor(
                                    color = newColor ?: getStatusBarColorBasedOnTheme(useDarkIcons),
                                    darkIcons = forcedUseDarkIcons ?: useDarkIcons
                                )
                            }

                        SideEffect {
                            if (useDarkIcons) {
                                systemUiController.setStatusBarColor(
                                    color = Color.White,
                                    darkIcons = true
                                )
                            } else {
                                systemUiController.setStatusBarColor(
                                    color = Color.Transparent,
                                    darkIcons = false
                                )
                            }
                            systemUiController.setNavigationBarColor(
                                originalNavBarColor,
                                darkIcons = useDarkIcons
                            )
                        }

                        MainScreen(
                            onNavBarColorChange = setCustomNavBarColor,
                            onStatusBarColorChange = setCustomStatusBarColor,
                            setDefaultNavBarColor = setDefaultNavBarColor,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }

    private fun getStatusBarColorBasedOnTheme(useDarkIcons: Boolean) =
        if (useDarkIcons) Color.White else Color.Transparent
}
