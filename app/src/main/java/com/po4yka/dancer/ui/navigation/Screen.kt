package com.po4yka.dancer.ui.navigation

import com.po4yka.dancer.R

sealed class Screen(
    val route: String,
    val iconId: Int,
    val title: String,
    val contentDescription: String,
) {
    object Gallery : Screen("gallery", R.drawable.ic_image_light, "Gallery", "")
    object Settings : Screen("settings", R.drawable.ic_setting_light, "Settings", "")
    object Camera : Screen("camera", R.drawable.ic_video_light, "", "")
}
