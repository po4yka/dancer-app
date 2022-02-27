package com.po4yka.dancer.models

import androidx.annotation.StringRes
import com.po4yka.dancer.R

enum class RecognitionModelName(
    @StringRes val movementNameId: Int
) {
    MOVEMENT_DAB_LEFT(R.string.movement_dab_left),
    MOVEMENT_DAB_RIGHT(R.string.movement_dab_right),
    MOVEMENT_LOTTERY_1(R.string.movement_lottery_1),
    MOVEMENT_LOTTERY_2_RIGHT(R.string.movement_lottery_2_right),
    MOVEMENT_LOTTERY_2_LEFT(R.string.movement_lottery_2_left),
    MOVEMENT_SAY_SO_1_LEFT(R.string.movement_say_so_1_left),
    MOVEMENT_SAY_SO_1_RIGHT(R.string.movement_say_so_1_right),
    MOVEMENT_SAY_SO_2(R.string.movement_say_so_2),
    MOVEMENT_WAP_1_LEFT(R.string.movement_wap_1_left),
    MOVEMENT_WAP_1_RIGHT(R.string.movement_wap_1_right),
    MOVEMENT_WAP_2(R.string.movement_wap_2),
    MOVEMENT_WAP_3_LEFT(R.string.movement_wap_3_left),
    MOVEMENT_WAP_3_RIGHT(R.string.movement_wap_3_right),
    MOVEMENT_WAP_4_LEFT(R.string.movement_wap_4_left),
    MOVEMENT_WAP_4_RIGHT(R.string.movement_wap_4_right)
}
