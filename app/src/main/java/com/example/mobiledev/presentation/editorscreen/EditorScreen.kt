package com.example.mobiledev.presentation.editorscreen

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.mobiledev.R
import com.example.mobiledev.presentation.editorscreen.common.IconButton
import com.example.mobiledev.presentation.editorscreen.common.Slider
import com.example.mobiledev.presentation.editorscreen.common.sliderElement
import com.example.mobiledev.presentation.navgraph.Route

@Composable
fun EditorScreen(
    navController: NavController,
    number:Int
){
    val viewModel = viewModel<EditorScreenViewModel>()

    val pickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = {uri-> viewModel.selectedImageUri = uri}
    )

    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ){
            IconButton(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(horizontal = 5.dp, vertical = 5.dp),
                onClick = {
                    navController.navigate(Route.MenuScreen.route + "/${number}")
                },
                icon = R.drawable.ic_menu,
                paramClick = number
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.85f)
                    .align(Alignment.Center)
            ){
                AsyncImage(
                    model = viewModel.selectedImageUri,
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.8f)
                )

                Spacer(Modifier.fillMaxHeight(0.2f))

                if(viewModel.selectedImageUri != null){
                    Slider(
                        modifier = Modifier.fillMaxSize(),
                        items = sliderElelements
                    )
                }
            }

            IconButton(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(horizontal = 5.dp, vertical = 5.dp),
                icon = R.drawable.ic_pic,
                onClick = {
                    pickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
                paramClick = null
            )
        }
    }
}


val sliderElelements = listOf(
    sliderElement(0, R.drawable.ic_rotate,R.string.rotate),
    sliderElement(1, R.drawable.ic_scale,R.string.scale),
    sliderElement(2, R.drawable.ic_filter,R.string.filters),
    sliderElement(3, R.drawable.ic_filter,R.string.filters),
    sliderElement(4, R.drawable.ic_filter,R.string.filters),
    sliderElement(5, R.drawable.ic_retouch,R.string.retouching),
    sliderElement(6, R.drawable.ic_spiral,R.string.mask),
)