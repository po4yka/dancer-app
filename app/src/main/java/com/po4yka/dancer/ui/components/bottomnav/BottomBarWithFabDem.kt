package com.po4yka.dancer.ui.components.bottomnav

import androidx.compose.foundation.layout.padding
import androidx.compose.material.FabPosition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.insets.ui.Scaffold
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.po4yka.dancer.navigation.NavigationFloatingButton
import com.po4yka.dancer.navigation.Router
import com.po4yka.dancer.ui.screens.MainScreenNavigation

@Composable
@androidx.camera.core.ExperimentalGetImage
@ExperimentalPermissionsApi
fun BottomBarWithFabDem(
    onNavBarColorChange: (newColor: Color?, forcedUseDarkIcons: Boolean?) -> Unit,
    onStatusBarColorChange: (newColor: Color?, forcedUseDarkIcons: Boolean?) -> Unit,
    setDefaultNavBarColor: () -> Unit,
    modifier: Modifier = Modifier,
    externalRouters: Map<String, Router> = emptyMap(),
) {
    // Stored in memory, NavHostController
    // Live through recompose and configuration changed cycle by rememberSavable
    val navController = rememberNavController()

    var bottomBarState by rememberSaveable { (mutableStateOf(true)) }

    Scaffold(
        bottomBar = {
            BottomBar(navController, bottomBarState, modifier)
        },
        floatingActionButtonPosition = FabPosition.Center,
        isFloatingActionButtonDocked = true,
        floatingActionButton = {
            NavigationFloatingButton(navController, bottomBarState)
        },
    ) { contentPadding ->
        MainScreenNavigation(
            navController = navController,
            externalRouters = externalRouters,
            onNavBarColorChange = onNavBarColorChange,
            onStatusBarColorChange = onStatusBarColorChange,
            setDefaultNavBarColor = setDefaultNavBarColor,
            onBottomBarStateChanged = { isVisible -> bottomBarState = isVisible },
            modifier = modifier.padding(contentPadding),
        )
    }
}
