package com.po4yka.dancer.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomAppBar
import androidx.compose.material.FabPosition
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.po4yka.dancer.ui.navigation.BottomNav
import com.po4yka.dancer.ui.navigation.MainScreenNavigation
import com.po4yka.dancer.ui.navigation.Screen
import com.po4yka.dancer.ui.theme.Azure100
import com.po4yka.dancer.ui.theme.SteelGray500

@Composable
fun BottomBarWithFabDem(navController: NavHostController) {
    Scaffold(
        bottomBar = {
            BottomAppBar(
                modifier = Modifier
                    .height(65.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(15.dp, 15.dp, 0.dp, 0.dp)),
                cutoutShape = CircleShape,
                elevation = 0.dp,
                backgroundColor = SteelGray500
            ) {
                BottomNav(navController = navController)
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        isFloatingActionButtonDocked = true,
        floatingActionButton = {
            FloatingActionButton(
                shape = CircleShape,
                onClick = {
                    Screen.Camera.route.let {
                        navController.navigate(it) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                    Screen.Camera.route.let { navController.navigate(it) }
                },
                backgroundColor = Azure100,
                contentColor = Color.White
            ) {
                Icon(
                    modifier = Modifier,
                    painter = painterResource(id = Screen.Camera.iconId),
                    contentDescription = Screen.Camera.contentDescription,
                    tint = Color.White
                )
            }
        }
    ) {
        MainScreenNavigation(navController)
    }
}

@Preview(showBackground = true)
@Composable
fun BottomBarWithFabDemPreview() {
    val navController = rememberNavController()
    BottomBarWithFabDem(navController)
}
