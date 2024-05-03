package com.example.mobiledev.presentation.editorscreen.common

data class SettingsItems(
    val numOfSliders:Int,
    val text:List<String>,
    val range:List<Pair<Int,Int>>
)