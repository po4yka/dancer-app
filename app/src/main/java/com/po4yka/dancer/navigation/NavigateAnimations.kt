package com.po4yka.dancer.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

@ExperimentalAnimationApi
@Composable
fun PresentModal(content: @Composable () -> Unit) {
    AnimatedVisibility(
        visibleState =
            remember {
                MutableTransitionState(false)
            }
                .apply { targetState = true },
        modifier = Modifier,
        enter =
            slideInVertically(initialOffsetY = { NavigateAnimations.initialOffset }) +
                fadeIn(initialAlpha = NavigateAnimations.initialAlpha),
        exit = slideOutVertically() + fadeOut(),
    ) {
        content()
    }
}

@ExperimentalAnimationApi
@Composable
fun PresentNested(content: @Composable () -> Unit) {
    AnimatedVisibility(
        visibleState =
            remember {
                MutableTransitionState(
                    initialState = false,
                )
            }
                .apply { targetState = true },
        modifier = Modifier,
        enter = slideInHorizontally(initialOffsetX = { it }) + fadeIn(initialAlpha = 0.3f),
        exit = slideOutHorizontally() + fadeOut(),
    ) {
        content()
    }
}

private object NavigateAnimations {
    const val initialOffset = 50
    const val initialAlpha = 0.3f
}
