package com.example.mobiledev.data.settingsitems

import com.example.mobiledev.R

val settingsItemsList = listOf(
    SettingsItems(1, listOf(R.string.rotate_angle), listOf<Pair<Int,Int>>(Pair(-180, 180))),
    SettingsItems(1, listOf(R.string.coef), listOf<Pair<Int,Int>>(Pair(50, 200))),
    SettingsItems(1, listOf(R.string.coef), listOf<Pair<Int,Int>>(Pair(-100, 100))),
    SettingsItems(0, listOf(), listOf<Pair<Int,Int>>()),
    SettingsItems(0, listOf(), listOf<Pair<Int,Int>>()),
    SettingsItems(1, listOf(R.string.radius), listOf<Pair<Int,Int>>(Pair(1, 100))),
    SettingsItems(1, listOf(R.string.seam_num,), listOf<Pair<Int,Int>>(Pair(1, 100))),
    SettingsItems(3, listOf(R.string.threshold, R.string.radius, R.string.amount),
        listOf<Pair<Int,Int>>(Pair(0, 255), Pair(0, 100), Pair(0, 50))),
)