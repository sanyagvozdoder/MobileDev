package com.example.mobiledev.presentation.editorscreen.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource

@Composable
fun IconButton(
    modifier: Modifier = Modifier,
    icon:Int,
    onClick:()->Unit,
    isEnabled:Boolean = true,
    color:Color = MaterialTheme.colorScheme.primary
){
    Button(
        modifier = modifier,
        onClick = onClick,
        enabled = isEnabled,
        colors = ButtonDefaults.buttonColors(containerColor = color)
    ){
        Icon(
            painter = painterResource(id = icon),
            contentDescription = null,
            modifier = Modifier.size(AssistChipDefaults.IconSize)
        )
    }
}