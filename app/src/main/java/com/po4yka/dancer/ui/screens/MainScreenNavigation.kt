package com.po4yka.dancer.ui.screens

import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.po4yka.dancer.navigation.NavScreen
import com.po4yka.dancer.navigation.Router
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalPermissionsApi
@ExperimentalCoroutinesApi
@ExperimentalFoundationApi
@androidx.camera.core.ExperimentalGetImage
@Composable
fun MainScreenNavigation(
    navController: NavHostController,
    externalRouters: Map<String, Router>,
    onBottomBarStateChanged: (Boolean) -> Unit,
    onNavBarColorChange: (newColor: Color, forcedUseDarkIcons: Boolean?) -> Unit,
    setDefaultNavBarColor: () -> Unit,
    modifier: Modifier = Modifier
) {

    NavHost(navController = navController, startDestination = NavScreen.Gallery.route) {
        composable(NavScreen.Gallery.route) {
            LaunchedEffect(Unit) {
                onBottomBarStateChanged.invoke(true)
            }

            val backDispatcher = LocalOnBackPressedDispatcherOwner.current
            DisposableEffect(Unit) {
                val callback = object : OnBackPressedCallback(true) {
                    override fun handleOnBackPressed() {
                        setDefaultNavBarColor.invoke()
                        navController.popBackStack()
                    }
                }
                backDispatcher?.onBackPressedDispatcher?.addCallback(callback)
                onDispose {
                    callback.remove()
                }
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

            val backDispatcher = LocalOnBackPressedDispatcherOwner.current
            DisposableEffect(Unit) {
                val callback = object : OnBackPressedCallback(true) {
                    override fun handleOnBackPressed() {
                        setDefaultNavBarColor.invoke()
                        navController.popBackStack()
                    }
                }
                backDispatcher?.onBackPressedDispatcher?.addCallback(callback)
                onDispose {
                    callback.remove()
                }
            }

            SettingsScreen(modifier = modifier)
        }
        composable(NavScreen.Camera.route) {
            LaunchedEffect(Unit) {
                onBottomBarStateChanged.invoke(false)
                onNavBarColorChange.invoke(Color.Transparent, false)
            }

            val backDispatcher = LocalOnBackPressedDispatcherOwner.current
            DisposableEffect(Unit) {
                val callback = object : OnBackPressedCallback(true) {
                    override fun handleOnBackPressed() {
                        setDefaultNavBarColor.invoke()
                        navController.popBackStack()
                    }
                }
                backDispatcher?.onBackPressedDispatcher?.addCallback(callback)
                onDispose {
                    callback.remove()
                }
            }

            CameraScreen(modifier = modifier)
        }
    }
}
