package com.po4yka.dancer.utils

import android.view.Window
import androidx.core.view.WindowCompat

fun Window.edgeToEdge(enabled: Boolean = true) {
    WindowCompat.setDecorFitsSystemWindows(this, !enabled)
}
