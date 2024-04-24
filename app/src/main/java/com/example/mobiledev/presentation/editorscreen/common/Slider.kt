package com.example.mobiledev.presentation.editorscreen.common

import android.net.Uri
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.mobiledev.presentation.editorscreen.EditorScreenViewModel

@Composable
fun Slider(
    items:List<sliderElement>,
    modifier: Modifier = Modifier,
    onItemClick: List<(ByteArray?, EditorScreenViewModel) -> Unit>,
    vmInst: EditorScreenViewModel,
    img: ByteArray?
){
    LazyRow(
        modifier = modifier
    ){
        (0..items.size-1).forEach{index->
            item{
                SliderItem(
                    icon = items[index].icon,
                    text = items[index].text,
                    onClick = onItemClick[index],
                    vmInst = vmInst,
                    img = img
                )
            }
        }
    }
}


data class sliderElement(
    val index:Int,
    @DrawableRes val icon:Int,
    @StringRes val text:Int
)

