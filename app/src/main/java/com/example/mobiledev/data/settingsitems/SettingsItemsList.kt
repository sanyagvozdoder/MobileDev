package com.example.mobiledev.data.settingsitems

val settingsItemsList = listOf(
    SettingsItems(1, listOf("Угол"), listOf<Pair<Int,Int>>(Pair(-180, 180))),
    SettingsItems(1, listOf("Коэфицент масштабирования"), listOf<Pair<Int,Int>>(Pair(50, 200))),
    SettingsItems(1, listOf("Коэфицент контраста"), listOf<Pair<Int,Int>>(Pair(-100, 100))),
    SettingsItems(0, listOf(), listOf<Pair<Int,Int>>()),
    SettingsItems(0, listOf(), listOf<Pair<Int,Int>>()),
    SettingsItems(0, listOf(), listOf<Pair<Int,Int>>()),
    SettingsItems(3, listOf("Порог", "Радиус", "Количество"),
        listOf<Pair<Int,Int>>(Pair(0, 255), Pair(0, 100), Pair(0, 50))),
    SettingsItems(1, listOf("Итераций",), listOf<Pair<Int,Int>>(Pair(1, 100))),
)