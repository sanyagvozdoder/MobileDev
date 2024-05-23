package com.example.mobiledev.presentation.retouchscreen

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.mobiledev.R
import com.example.mobiledev.presentation.algoritms.MotionEvent
import com.example.mobiledev.presentation.algoritms.applyRetouch
import com.example.mobiledev.presentation.editorscreen.common.IconButton
import com.example.mobiledev.presentation.editorscreen.menuitems
import com.example.mobiledev.presentation.editorscreen.readBytes
import com.example.mobiledev.presentation.sidebar.common.SideBarItem
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RetouchScreen(
    navController:NavController
){
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val viewModel = viewModel<RetouchScreenViewModel>()

    val stateUri by viewModel.stateUriFlow.collectAsState()

    val dots = remember{
        mutableStateListOf<Offset>()
    }

    var currentRadius by remember {
        mutableStateOf(30)
    }

    var workSpaceSize by remember { mutableStateOf(IntSize.Zero) }

    val context = LocalContext.current

    var motionEvent by remember { mutableStateOf(MotionEvent.Idle) }
    var currentPosition by remember { mutableStateOf(Offset.Unspecified) }
    var previousPosition by remember { mutableStateOf(Offset.Unspecified) }

    val pickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri->
            viewModel.onStateUpdate(uri)
        }
    )


    ModalNavigationDrawer(
        drawerContent = {
            ModalDrawerSheet {
                menuitems.forEachIndexed{ index, item->
                    NavigationDrawerItem(
                        label = {
                            SideBarItem(
                                icon = item.icon,
                                text = item.text
                            )
                        },
                        selected = false,
                        onClick = {
                            navController.navigate(item.route)
                            scope.launch {
                                drawerState.close()
                            }
                        }
                    )
                }
            }
        },
        drawerState = drawerState
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(text = "Ретуширование", color = Color.White)
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                scope.launch {
                                    drawerState.open()
                                }
                            },
                            icon = R.drawable.ic_menu
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                    )
                )
            },
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = it.calculateTopPadding() + 10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(2f)
                        .fillMaxHeight()
                        .fillMaxWidth(0.9f)
                        .align(Alignment.CenterHorizontally)
                        .pointerInput(Unit) {
                            detectTapGestures {
                                dots.add(it)
                                //Log.d("ЖОПА", getMaskFactor(dots, currentRadius, it).toString())
                            }
                        }
                ){
                    AsyncImage(
                        model = stateUri.currentValue.value,
                        contentDescription = null,
                        contentScale = ContentScale.FillBounds,
                        modifier = Modifier.fillMaxSize()
                    )
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .alpha(0.7f)
                            .onGloballyPositioned { coordinates ->
                                workSpaceSize = coordinates.size
                            }
                            .pointerInput(Unit) {
                                /*detectDragGestures(
                                    onDragStart = { offset ->
                                        dots.add(offset)
                                        currentPosition = offset
                                        motionEvent = MotionEvent.Down
                                    },
                                    onDragEnd = {
                                        applyRetouch(readBytes(context, stateUri.currentValue.value),
                                            viewModel, dots, currentRadius, workSpaceSize)
                                        motionEvent = MotionEvent.Up
                                        dots.clear()
                                    }
                                ) { change, dragAmount ->
                                    dots.add(change.position)
                                    currentPosition = change.position
                                    motionEvent = MotionEvent.Move
                                    change.consume()
                                }*/
                            }
                    ){
                        dots.forEachIndexed{ index, dot ->
                            /*if(index > 0)
                            {
                                drawLine(
                                    Color(0f, 0f, 0f),
                                    dots[index - 1],
                                    dots[index],
                                    strokeWidth = currentRadius.toFloat(),
                                    cap = StrokeCap.Round
                                )
                            }*/
                            drawCircle(
                                color = Color.Black,
                                radius = currentRadius.toFloat(),
                                center = dot
                            )
                        }
                        /*drawPoints(
                            dots,
                            PointMode.Lines,
                            color = Color(0f, 0f, 0f, 0.7f),
                            strokeWidth = currentRadius.toFloat()
                        )*/
                    }
                }
                Row(
                    modifier = Modifier
                        .weight(0.3f)
                        .fillMaxSize()
                ) {
                    IconButton(
                        icon = R.drawable.ic_pic,
                        onClick = {
                            pickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                    )
                }
                Row {
                    Text(text = "Радиус кисти: " + currentRadius.toString(),
                        modifier = Modifier.fillMaxWidth(0.2f))
                    androidx.compose.material3.Slider(
                        value = currentRadius.toFloat(),
                        onValueChange = {newValue->
                            currentRadius = newValue.toInt()
                        },
                        valueRange = 1f..200f,
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }
                Row {
                    Button(
                        onClick = {
                            applyRetouch(readBytes(context, stateUri.currentValue.value),
                                viewModel, dots, currentRadius, workSpaceSize)
                            dots.clear()
                        }
                    ) {
                        Text(text = "launch")
                    }
                }
            }
        }
    }
}
