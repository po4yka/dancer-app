package com.po4yka.dancer.ui.components.models

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class ButtonActionRes(
    @DrawableRes val actionIconId: Int,
    @StringRes val actionDescriptionStrId: Int
)
