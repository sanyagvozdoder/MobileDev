package com.example.mobiledev.presentation.editorscreen.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.mobiledev.R

@Composable
fun SettingsTools(
    modifier: Modifier = Modifier,
    numberOfSliders:Int = 0,
    onAcceptClick:()->Unit,
    onBackClick:()->Unit
){
    val sliderPositions = remember { mutableStateListOf<Float>().apply {
        repeat(numberOfSliders) {
            add(100f)
        }
    }}

    val animatedTextStates = remember { mutableStateListOf<Boolean>().apply {
        repeat(numberOfSliders) {
            add(false)
        }
    }}

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            Button(
                modifier = Modifier
                    .background(color = Color.Transparent),
                onClick = {onBackClick()}) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_close),
                    contentDescription = null,
                    modifier = Modifier.size(AssistChipDefaults.IconSize)
                )
            }
            Button(
                modifier = Modifier
                    .background(color = Color.Transparent),
                onClick = {onAcceptClick()}
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_accept),
                    contentDescription = null,
                    modifier = Modifier.size(AssistChipDefaults.IconSize)
                )
            }
        }

        (0..numberOfSliders-1).forEach {index->
            AnimatedVisibility(visible = animatedTextStates[index]) {
                Text(
                    text = sliderPositions[index].toInt().toString(),
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                )
            }

            androidx.compose.material3.Slider(
                value = sliderPositions[index],
                onValueChange = {newValue->
                    sliderPositions[index] = newValue
                    animatedTextStates[index] = true
                },
                valueRange = 50f..200f,
                modifier = Modifier.fillMaxWidth(0.8f).padding(vertical = 8.dp)
            )
        }
    }
}