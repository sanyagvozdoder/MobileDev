package com.example.mobiledev.presentation.editorscreen

import android.annotation.SuppressLint
import android.app.PendingIntent.getActivity
import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.mobiledev.R
import com.example.mobiledev.presentation.algoritms.Rotate
import com.example.mobiledev.presentation.algoritms.Scaling
import com.example.mobiledev.presentation.editorscreen.common.IconButton
import com.example.mobiledev.presentation.editorscreen.common.Slider
import com.example.mobiledev.presentation.editorscreen.common.sliderElement
import com.example.mobiledev.presentation.navgraph.Route
import kotlinx.coroutines.CoroutineScope
import java.io.IOException
import java.security.AccessController.getContext
import androidx.compose.runtime.Composable as Composable

@Composable
fun EditorScreen(
    navController: NavController,
    number:Int
){
    val editViewModel = viewModel<EditorScreenViewModel>()
    val stateUri by editViewModel.stateUriFlow.collectAsState()
    val sliderState by editViewModel.isSliderVisible.collectAsState()

    val pickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = {uri-> editViewModel.onStateUpdate(uri)}
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
                    model = stateUri,
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.8f)
                )

                Spacer(Modifier.fillMaxHeight(0.2f))

                //state of screen
                //1 state: no image(uri == null)
                //2 state: image, + slider (SLiderItem,onclick)
                //3 state: settings of filter + 2 buttons (exit mark -> back to 2 state)

                AnimatedVisibility(
                    visible = sliderState
                ) {
                    Slider(
                        modifier = Modifier.fillMaxSize(),
                        items = sliderElelements,
                        vmInst = editViewModel
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
                    editViewModel.onSliderStateUpdate(true)
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

val functionsAlghoritms = listOf<(ByteArray?, EditorScreenViewModel) -> Unit>(
    ::Rotate,
    ::Scaling,
    ::Scaling,
    ::Scaling,
    ::Scaling,
    ::Scaling,
    ::Scaling
)

@Throws(IOException::class)
private fun readBytes(context: Context, uri: Uri?): ByteArray? =
    uri?.let { context.contentResolver.openInputStream(it)?.use { it.buffered().readBytes() } }
