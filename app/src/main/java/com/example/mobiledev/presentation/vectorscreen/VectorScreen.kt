package com.example.mobiledev.presentation.vectorscreen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mobiledev.R
import com.example.mobiledev.presentation.algoritms.DrawSpline
import com.example.mobiledev.presentation.algoritms.OnTap
import com.example.mobiledev.presentation.algoritms.util.SplineDot
import com.example.mobiledev.presentation.algoritms.util.SplineMode
import com.example.mobiledev.presentation.algoritms.util.VectorScreenMode
import com.example.mobiledev.presentation.editorscreen.common.IconButton
import com.example.mobiledev.presentation.sidebar.common.SideBarItem
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VectorScreen(
    navController: NavController
) {
    val viewModel = viewModel<VectorScreenViewModel>()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val dots = remember {
        mutableStateListOf<SplineDot>()
    }
    var selectedDot by remember {
        mutableStateOf(-1)
    }

    var selectionMode by remember {
        mutableStateOf(false) // false - dot, true - anchor
    }

    var checkedScreen by remember { mutableStateOf(true) }
    var checkedSpline by remember { mutableStateOf(false) }

    var showBottomSheet by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()

    val screenMode = viewModel.screenModeState.collectAsState()
    var splineMode = viewModel.splineModeState.collectAsState()

    val color = MaterialTheme.colorScheme.onSurface

    ModalNavigationDrawer(
        drawerContent = {
            ModalDrawerSheet {
                viewModel.getMenuItems().forEachIndexed { index, item ->
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
                        Text(text = stringResource(id = R.string.vector), color = Color.White)
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
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    Row(
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .height(with(LocalDensity.current) { 10.sp.toDp() * 5 })
                    ) {
                        if (screenMode.value == VectorScreenMode.DRAW)
                            Text(
                                text = stringResource(id = R.string.draw_mode_hint),
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center,
                                fontSize = 10.sp
                            )
                        else
                            Text(
                                text = stringResource(R.string.edit_mode_hint),
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center,
                                fontSize = 10.sp
                            )
                    }

                    Canvas(
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .fillMaxHeight(0.9f)
                            .padding(vertical = 8.dp)
                            .align(Alignment.CenterHorizontally)
                            .border(2.dp, MaterialTheme.colorScheme.onSurface)
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onDoubleTap = {
                                        selectionMode = !selectionMode
                                    },
                                    onLongPress = { offset ->
                                        if (screenMode.value == VectorScreenMode.EDIT) {
                                            if (selectedDot != -1) {
                                                if (selectionMode == false)
                                                    dots[selectedDot] =
                                                        SplineDot(offset, dots[selectedDot].anchor)
                                                else
                                                    dots[selectedDot] =
                                                        SplineDot(
                                                            dots[selectedDot].position,
                                                            offset
                                                        )
                                            }
                                        }
                                    }) { offset ->
                                    if (screenMode.value == VectorScreenMode.DRAW) {
                                        if (dots.size > 0) {
                                            val prevDot = dots[dots.size - 1]
                                            prevDot.anchor =
                                                prevDot.position - (prevDot.position - offset) / 2f
                                        }
                                        dots.add(SplineDot(offset, offset))
                                        dots[dots.size - 1].anchor =
                                            dots[0].position - (dots[0].position - offset) / 2f
                                    } else {
                                        selectionMode = false
                                        OnTap(dots, offset, { i ->
                                            selectedDot = i
                                        })
                                    }
                                }
                            }
                    ) {
                        DrawSpline(
                            dots,
                            screenMode.value,
                            selectedDot,
                            selectionMode,
                            splineMode.value,
                            color
                        )
                    }
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(vertical = 5.dp, horizontal = 10.dp)
                    ) {
                        Switch(
                            checked = checkedScreen,
                            onCheckedChange = {
                                if (it == true) {
                                    viewModel.onScreenModeUpdate(VectorScreenMode.DRAW)
                                } else {
                                    viewModel.onScreenModeUpdate(VectorScreenMode.EDIT)
                                }
                                checkedScreen = it
                            },
                            thumbContent = if (checkedScreen) {
                                {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_dot),
                                        contentDescription = null,
                                        modifier = Modifier.size(SwitchDefaults.IconSize),
                                    )
                                }
                            } else {
                                {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_edit),
                                        contentDescription = null,
                                        modifier = Modifier.size(SwitchDefaults.IconSize),
                                    )
                                }
                            }
                        )
                        Button(onClick = {
                            showBottomSheet = true
                        }) {
                            Text(text = stringResource(id = R.string.remove_menu))
                        }
                        Switch(
                            checked = checkedSpline,
                            onCheckedChange = {
                                if (it == true) {
                                    viewModel.onSplineModeUpdate(SplineMode.SHAPE)
                                } else {
                                    viewModel.onSplineModeUpdate(SplineMode.LINE)
                                }
                                checkedSpline = it
                            },
                            thumbContent = if (checkedSpline) {
                                {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_shape),
                                        contentDescription = null,
                                        modifier = Modifier.size(SwitchDefaults.IconSize),
                                    )
                                }
                            } else {
                                {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_line),
                                        contentDescription = null,
                                        modifier = Modifier.size(SwitchDefaults.IconSize),
                                    )
                                }
                            }
                        )
                    }
                }
            }
            if (showBottomSheet) {
                ModalBottomSheet(
                    onDismissRequest = {
                        showBottomSheet = false
                    },
                    sheetState = sheetState
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Top
                    ) {
                        Button(
                            onClick = {
                                dots.clear()
                                viewModel.onScreenModeUpdate(VectorScreenMode.DRAW)
                                selectedDot = -1
                                selectionMode = false
                            },
                            modifier = Modifier.padding(vertical = 10.dp)
                        ) {
                            Text(text = stringResource(id = R.string.clear_all))
                        }
                        if (selectedDot != -1) {
                            Button(
                                onClick = {
                                    dots.removeAt(selectedDot)
                                    selectedDot = -1
                                    selectionMode = false
                                },
                                modifier = Modifier.padding(vertical = 10.dp)
                            ) {
                                Text(text = stringResource(id = R.string.remove_point))
                            }
                        }
                    }
                }
            }
        }
    }
}