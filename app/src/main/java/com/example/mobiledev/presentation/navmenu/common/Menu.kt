package com.example.mobiledev.presentation.navmenu.common

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun Menu(
    modifier: Modifier = Modifier,
    items:List<menuElement>,
    selected:Int,
    onItemClick:(Int)->Unit
){
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center) {
        items.forEach{item->
            Item(
                modifier = Modifier.fillMaxWidth(),
                selected = item.index == selected,
                onClick = onItemClick,
                icon = item.icon,
                text = item.text,
                id = item.index
            )
        }
    }
}

data class menuElement(
    val index:Int,
    @DrawableRes val icon:Int,
    @StringRes val text:Int
)
