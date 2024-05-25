package com.example.mobiledev.presentation.editorscreen.common

import android.content.Context
import android.net.Uri
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AssistChipDefaults.IconSize
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mobiledev.presentation.editorscreen.EditorScreenViewModel
import com.example.mobiledev.presentation.editorscreen.readBytes

@Composable
fun LazyItemScope.SliderItem(
    icon: Int,
    text: Int,
    vmInst: EditorScreenViewModel,
    index: Int,
    context: Context
) {
    val stateUri by vmInst.stateUriFlow.collectAsState()

    Column(
        modifier = Modifier
            .padding(horizontal = 5.dp)
    ) {
        Box(
            modifier = Modifier
                .border(1.dp, MaterialTheme.colorScheme.onSurface, CircleShape)
                .align(Alignment.CenterHorizontally)
                .size(IconSize * 3)
                .clickable(onClick = {
                    if (vmInst.getSettingsItems()[index].numOfSliders == 0) {
                        val lambda: (Uri?) -> Unit = { uri ->
                            vmInst.onStateUpdate(uri)
                        }
                        val byteArray = readBytes(context, stateUri.currentValue.value)

                        vmInst.getFunctionsList()[index].invoke(byteArray, lambda, listOf())
                    } else {
                        vmInst.onSliderStateUpdate(false)
                        vmInst.onSettingsStateUpdate(index)
                    }
                })
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
                modifier = Modifier
                    .size(IconSize * 2)
                    .align(Alignment.Center)
            )
        }
        Text(
            modifier = Modifier
                .padding(vertical = 5.dp)
                .align(Alignment.CenterHorizontally),
            text = stringResource(id = text),
            fontSize = 8.sp
        )
    }
}