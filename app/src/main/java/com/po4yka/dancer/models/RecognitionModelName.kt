package com.po4yka.dancer.models

import android.content.Context
import androidx.annotation.StringRes
import com.po4yka.dancer.R

enum class RecognitionModelName(
    @StringRes val movementNameId: Int
) {
    MOVEMENT_DAB_LEFT(R.string.dab_left),
    MOVEMENT_DAB_RIGHT(R.string.dab_right),
    MOVEMENT_LOTTERY_1(R.string.lottery_1),
    MOVEMENT_LOTTERY_2_RIGHT(R.string.lottery_2_right),
    MOVEMENT_LOTTERY_2_LEFT(R.string.lottery_2_left),
    MOVEMENT_SAY_SO_1_LEFT(R.string.say_so_1_left),
    MOVEMENT_SAY_SO_1_RIGHT(R.string.say_so_1_right),
    MOVEMENT_SAY_SO_2(R.string.say_so_2),
    MOVEMENT_WAP_1_LEFT(R.string.wap_1_left),
    MOVEMENT_WAP_1_RIGHT(R.string.wap_1_right),
    MOVEMENT_WAP_2(R.string.wap_2),
    MOVEMENT_WAP_3_LEFT(R.string.wap_3_left),
    MOVEMENT_WAP_3_RIGHT(R.string.wap_3_right),
    MOVEMENT_WAP_4_LEFT(R.string.wap_4_left),
    MOVEMENT_WAP_4_RIGHT(R.string.wap_4_right)
}

object RecognitionModelHelper {
    fun getClassesNames(context: Context): List<String> {
        return RecognitionModelName.values().map { context.resources.getString(it.movementNameId) }
    }
}
