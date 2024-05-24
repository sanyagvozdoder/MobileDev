package com.example.mobiledev.data.settingsitems

data class SettingsItems(
    val numOfSliders:Int,
    val text:List<String>,
    val range:List<Pair<Int,Int>>
)
