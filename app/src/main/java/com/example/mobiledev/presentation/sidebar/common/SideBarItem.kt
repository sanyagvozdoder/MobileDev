package com.example.mobiledev.presentation.sidebar.common

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SideBarItem(
    modifier: Modifier = Modifier,
    @DrawableRes icon:Int,
    @StringRes text:Int
){
    Row(){
        Icon(
            painter = painterResource(id = icon),
            contentDescription = null,
            modifier = Modifier.size(AssistChipDefaults.IconSize)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(text = stringResource(id = text), fontSize = 15.sp )
    }
}

data class sideBarElement(
    val id:Int,
    @DrawableRes val icon:Int,
    @StringRes val text:Int,
    val route:String
)