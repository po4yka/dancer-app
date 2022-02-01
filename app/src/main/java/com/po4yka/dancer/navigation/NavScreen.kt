package com.po4yka.dancer.navigation

import com.po4yka.dancer.R

sealed class NavScreen(
    val route: String,
    val iconId: Int,
    val title: String,
    val contentDescription: String,
) {
    object Main : NavScreen("main", -1, "", "")
    object ImageInfo : NavScreen("image_info", -1, "", "")

    object Gallery : NavScreen("gallery", R.drawable.ic_image_light, "Gallery", "")
    object Settings : NavScreen("settings", R.drawable.ic_setting_light, "Settings", "")
    object Camera : NavScreen("camera", R.drawable.ic_video_light, "", "")
}
