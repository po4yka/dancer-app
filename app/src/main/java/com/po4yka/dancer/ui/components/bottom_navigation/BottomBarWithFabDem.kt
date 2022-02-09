package com.po4yka.dancer.ui.components.bottom_navigation

import androidx.compose.material.FabPosition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.insets.ui.Scaffold
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.po4yka.dancer.navigation.NavigationFloatingButton
import com.po4yka.dancer.navigation.Router
import com.po4yka.dancer.ui.screens.MainScreenNavigation
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@ExperimentalPermissionsApi
@Composable
fun BottomBarWithFabDem(
    modifier: Modifier = Modifier,
    externalRouters: Map<String, Router> = emptyMap()
) {
    // Stored in memory, NavHostController
    // Live through recompose and configuration changed cycle by rememberSaveable
    val navController = rememberNavController()

    val bottomBarState = rememberSaveable { (mutableStateOf(true)) }

    Scaffold(
        bottomBar = {
            BottomBar(navController, bottomBarState, modifier)
        },
        floatingActionButtonPosition = FabPosition.Center,
        isFloatingActionButtonDocked = true,
        floatingActionButton = {
            NavigationFloatingButton(navController, bottomBarState)
        }
    ) {
        MainScreenNavigation(
            navController = navController,
            externalRouters = externalRouters,
            bottomBarState = bottomBarState,
            modifier = modifier
        )
    }
}
