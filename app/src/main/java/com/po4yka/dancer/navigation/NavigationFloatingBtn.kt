package com.po4yka.dancer.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.po4yka.dancer.ui.theme.Azure100

@Composable
fun NavigationFloatingButton(
    navController: NavController,
    isVisible: Boolean,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(initialOffsetY = { it }),
        exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
        content = {
            FloatingActionButton(
                shape = CircleShape,
                onClick = {
                    NavScreen.Camera.route.let {
                        navController.navigate(it) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                backgroundColor = Azure100,
                contentColor = Color.White,
                modifier = modifier
            ) {
                Icon(
                    modifier = Modifier,
                    painter = painterResource(id = NavScreen.Camera.iconId),
                    contentDescription = NavScreen.Camera.contentDescription,
                    tint = Color.White
                )
            }
        }
    )
}
