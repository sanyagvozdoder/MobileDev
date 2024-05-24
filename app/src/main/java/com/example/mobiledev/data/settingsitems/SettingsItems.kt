package com.example.mobiledev.data.settingsitems

import androidx.annotation.StringRes

data class SettingsItems(
    val numOfSliders:Int,
    @StringRes val text:List<Int>,
    val range:List<Pair<Int,Int>>
)
