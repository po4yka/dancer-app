package com.po4yka.dancer.models

import android.content.Context
import android.os.Parcelable
import androidx.annotation.StringRes
import com.po4yka.dancer.R
import kotlinx.parcelize.Parcelize

@Parcelize
enum class RecognitionModelName(
    val id: String,
    @StringRes val movementNameId: Int
) : Parcelable {
    MOVEMENT_DAB_LEFT("0", R.string.dab_left),
    MOVEMENT_DAB_RIGHT("1", R.string.dab_right),
    MOVEMENT_LOTTERY_1("2", R.string.lottery_1),
    MOVEMENT_LOTTERY_2_RIGHT("3", R.string.lottery_2_right),
    MOVEMENT_LOTTERY_2_LEFT("4", R.string.lottery_2_left),
    MOVEMENT_SAY_SO_1_LEFT("5", R.string.say_so_1_left),
    MOVEMENT_SAY_SO_1_RIGHT("6", R.string.say_so_1_right),
    MOVEMENT_SAY_SO_2("7", R.string.say_so_2),
    MOVEMENT_WAP_1_LEFT("8", R.string.wap_1_left),
    MOVEMENT_WAP_1_RIGHT("9", R.string.wap_1_right),
    MOVEMENT_WAP_2("10", R.string.wap_2),
    MOVEMENT_WAP_3_LEFT("11", R.string.wap_3_left),
    MOVEMENT_WAP_3_RIGHT("12", R.string.wap_3_right),
    MOVEMENT_WAP_4_LEFT("13", R.string.wap_4_left),
    MOVEMENT_WAP_4_RIGHT("14", R.string.wap_4_right)
}

object RecognitionModelHelper {

    fun getClassesNames(context: Context): List<String> {
        return RecognitionModelName.values().map { context.resources.getString(it.movementNameId) }
    }

    fun getClassesIds(): List<String> {
        return RecognitionModelName.values().map { it.id }
    }

    fun getClassById(id: String): RecognitionModelName {
        return RecognitionModelName.values().first { it.id == id }
    }
}
