package com.example.mobiledev.presentation.navmenu.common

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
fun Item(
    modifier: Modifier = Modifier,
    selected:Boolean,
    onClick:(Int)->Unit,
    @DrawableRes icon:Int,
    @StringRes text:Int,
    id:Int,
    selectedTextColor: Color = MaterialTheme.colorScheme.primary,
    unselectedTextColor: Color = MaterialTheme.colorScheme.secondary,
){
    Row(
        modifier = modifier.clickable(onClick = {onClick(id)})
    ){
        Icon(
            painter = painterResource(id = icon),
            contentDescription = null,
            modifier = Modifier.size(AssistChipDefaults.IconSize)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(text = stringResource(id = text), color = if(selected) selectedTextColor else unselectedTextColor, fontSize = 15.sp )
    }
}