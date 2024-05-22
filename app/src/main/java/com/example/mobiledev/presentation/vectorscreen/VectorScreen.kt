package com.example.mobiledev.presentation.vectorscreen

import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.mobiledev.R
import com.example.mobiledev.presentation.algoritms.DrawSpline
import com.example.mobiledev.presentation.algoritms.OnTap
import com.example.mobiledev.presentation.algoritms.util.SplineDot
import com.example.mobiledev.presentation.algoritms.util.SplineMode
import com.example.mobiledev.presentation.algoritms.util.VectorScreenMode
import com.example.mobiledev.presentation.editorscreen.common.IconButton
import com.example.mobiledev.presentation.editorscreen.common.SettingsTools
import com.example.mobiledev.presentation.editorscreen.common.Slider
import com.example.mobiledev.presentation.editorscreen.functionsAlghoritms
import com.example.mobiledev.presentation.editorscreen.menuitems
import com.example.mobiledev.presentation.editorscreen.settings
import com.example.mobiledev.presentation.editorscreen.sliderElelements
import com.example.mobiledev.presentation.navgraph.Route
import com.example.mobiledev.presentation.sidebar.common.SideBarItem
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VectorScreen(
    navController: NavController
){
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val dots = remember{
        mutableStateListOf<SplineDot>()
    }
    var selectedDot by remember {
        mutableStateOf(-1)
    }

    var selectionMode by remember {
        mutableStateOf(false) // false - dot, true - anchor
    }

    var mode by remember {
        mutableStateOf(VectorScreenMode.DRAW)
    }

    var splineMode by remember {
        mutableStateOf(SplineMode.LINE)
    }

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
                        Text(text = "Редактор сплайнов", color = Color.White)
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
                    .padding(vertical = it.calculateTopPadding() + 15.dp)
                    .fillMaxSize()
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .fillMaxHeight(0.75f)
                        .align(Alignment.CenterHorizontally)
                        .border(2.dp, Color.Black)
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onDoubleTap = {
                                    selectionMode = !selectionMode
                                },
                                onLongPress = { offset ->
                                    if (mode == VectorScreenMode.EDIT) {
                                        if (selectedDot != -1) {
                                            if (selectionMode == false)
                                                dots[selectedDot] =
                                                    SplineDot(offset, dots[selectedDot].anchor)
                                            else
                                                dots[selectedDot] =
                                                    SplineDot(dots[selectedDot].position, offset)
                                        }
                                    }
                                }) { offset ->
                                if (mode == VectorScreenMode.DRAW) {
                                    if (dots.size > 0) {
                                        val prevDot = dots[dots.size - 1]
                                        prevDot.anchor =
                                            prevDot.position - (prevDot.position - offset) / 2f
                                    }
                                    dots.add(SplineDot(offset, offset))
                                    dots[dots.size - 1].anchor = dots[0].position - (dots[0].position - offset) / 2f
                                } else {
                                    selectionMode = false
                                    OnTap(dots, offset, { i ->
                                        selectedDot = i
                                    })
                                }
                            }
                        }
                ){
                    DrawSpline(dots, mode, selectedDot, selectionMode, splineMode)
                }
                Row {
                    if (mode == VectorScreenMode.DRAW)
                    {
                        Button(
                            //modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                mode = VectorScreenMode.EDIT
                            }
                        ) {
                            Text(text = "Установка точек")
                        }
                    }
                    else
                    {
                        Button(
                            //modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                mode = VectorScreenMode.DRAW
                                selectedDot = -1
                                selectionMode = false
                            }
                        ) {
                            Text(text = "Редактирование")
                        }
                    }
                    if (splineMode == SplineMode.LINE)
                    {
                        Button(
                            //modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                splineMode = SplineMode.SHAPE
                            }
                        ) {
                            Text(text = "Линия")
                        }
                    }
                    else
                    {
                        Button(
                            //modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                splineMode = SplineMode.LINE
                            }
                        ) {
                            Text(text = "Фигура")
                        }
                    }
                }
                Row {
                    Button(
                        onClick = {
                            dots.clear()
                            mode = VectorScreenMode.DRAW
                            selectedDot = -1
                            selectionMode = false
                        }
                    ){
                        Text(text = "Очистить всё")
                    }
                    if(selectedDot != -1)
                    {
                        Button(
                            onClick = {
                                dots.removeAt(selectedDot)
                                selectedDot = -1
                                selectionMode = false
                            }
                        ){
                            Text(text = "Удалить точку")
                        }
                    }
                }
                Row {
                    if(mode == VectorScreenMode.DRAW)
                        Text(text = "Нажмите, чтобы разместить точку.")
                    else
                        Text(text = "Нажмите, чтобы выделить, удерживайте, чтобы переместить. " +
                                "Двойное нажатие - переключение якорь/точка.")
                }
            }
        }
    }
}