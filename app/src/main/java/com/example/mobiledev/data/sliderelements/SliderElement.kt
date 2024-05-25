package com.example.mobiledev.data.sliderelements

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class SliderElement(
    val index: Int,
    @DrawableRes val icon: Int,
    @StringRes val text: Int
)
