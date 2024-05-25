package com.example.mobiledev.data.sidebarmenu

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class SideBarElement(
    val id: Int,
    @DrawableRes val icon: Int,
    @StringRes val text: Int,
    val route: String
)
