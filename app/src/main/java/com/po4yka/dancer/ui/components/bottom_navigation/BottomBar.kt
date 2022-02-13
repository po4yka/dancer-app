package com.po4yka.dancer.ui.components.bottom_navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.po4yka.dancer.navigation.NavScreen
import com.po4yka.dancer.ui.theme.Sky300
import com.po4yka.dancer.ui.theme.SteelGray500

@Composable
fun BottomBar(
    navController: NavController,
    isVisible: Boolean,
    modifier: Modifier = Modifier
) {

    val navItems = listOf(
        NavScreen.Gallery,
        NavScreen.Settings
    )

    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(initialOffsetY = { it }),
        exit = slideOutVertically(targetOffsetY = { it }),
        content = {
            BottomAppBar(
                modifier = modifier
                    .height(65.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(15.dp, 15.dp, 0.dp, 0.dp)),
                cutoutShape = CircleShape,
                elevation = 0.dp,
                backgroundColor = SteelGray500,
            ) {
                BottomNavigation(
                    modifier = Modifier
                        .height(115.dp)
                        .fillMaxWidth(),
                    elevation = 0.dp,
                    backgroundColor = SteelGray500
                ) {

                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute = navBackStackEntry?.destination?.route

                    navItems.forEach { navItem ->

                        BottomNavigationItem(
                            icon = {
                                Icon(
                                    painter = painterResource(id = navItem.iconId),
                                    contentDescription = navItem.contentDescription,
                                    modifier = Modifier.size(30.dp)
                                )
                            },
                            label = {
                                Text(
                                    modifier = Modifier,
                                    text = navItem.title
                                )
                            },
                            selected = currentRoute == navItem.route,

                            selectedContentColor = Sky300,
                            unselectedContentColor = Color.White.copy(alpha = 0.4f),
                            alwaysShowLabel = true,
                            onClick = {
                                navItem.route.let { route ->
                                    navController.navigate(route) {
                                        // Pop up to the start destination of the graph to
                                        // avoid building up a large stack of destinations
                                        // on the back stack as users select items
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        // Avoid multiple copies of the same destination when
                                        // re-selecting the same item
                                        launchSingleTop = true
                                        // Restore state when re-selecting a previously selected item
                                        restoreState = true
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    )
}
