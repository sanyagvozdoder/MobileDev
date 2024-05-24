package com.example.mobiledev.presentation.editorscreen.common

import android.net.Uri
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.mobiledev.data.sliderelements.SliderElement
import com.example.mobiledev.presentation.editorscreen.EditorScreenViewModel
import kotlinx.coroutines.CoroutineScope

@Composable
fun Slider(
    items:List<SliderElement>,
    modifier: Modifier = Modifier,
    vmInst: EditorScreenViewModel
){
    LazyRow(
        modifier = modifier
    ){
        (0..items.size-1).forEach{index->
            item{
                SliderItem(
                    icon = items[index].icon,
                    text = items[index].text,
                    vmInst = vmInst,
                    index = index
                )
            }
        }
    }
}

