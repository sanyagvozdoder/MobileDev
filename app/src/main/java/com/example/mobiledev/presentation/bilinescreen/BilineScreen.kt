package com.example.mobiledev.presentation.bilinescreen

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.mobiledev.R
import com.example.mobiledev.presentation.editorscreen.common.IconButton
import com.example.mobiledev.presentation.sidebar.common.SideBarItem
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BilineScreen(
    navController:NavController
){
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val viewModel = viewModel<BilineScreenViewModel>()

    val startUri by viewModel.startUriFlow.collectAsState()
    val endUri by viewModel.endUriFlow.collectAsState()

    val dotsStart = remember{
        mutableStateListOf<Offset>()
    }
    val dotsEnd = remember{
        mutableStateListOf<Offset>()
    }

    val pickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri->
            viewModel.onStartUpdate(uri)
            viewModel.onEndUpdate(uri)
        }
    )


    ModalNavigationDrawer(
        drawerContent = {
            ModalDrawerSheet {
                viewModel.getMenuItems().forEachIndexed{ index, item->
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
                        Text(text = stringResource(id = R.string.biline), color = Color.White)
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
                                if (dotsStart.size <= 2) {
                                    dotsStart.add(it)
                                }
                            }
                        }
                ){
                    AsyncImage(
                        model = startUri,
                        contentDescription = null,
                        contentScale = ContentScale.FillBounds,
                        modifier = Modifier.fillMaxSize()
                    )
                    Canvas(
                        modifier = Modifier.fillMaxSize()
                    ){
                        dotsStart.forEachIndexed{index, offset->
                            drawCircle(
                                color = if(index == 0) Color.Red else if (index == 1) Color.Green else Color.Blue,
                                radius = 20f,
                                center = offset
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .weight(2f)
                        .fillMaxHeight()
                        .fillMaxWidth(0.9f)
                        .align(Alignment.CenterHorizontally)
                        .pointerInput(Unit) {
                            detectTapGestures {
                                if (dotsEnd.size <= 2) {
                                    dotsEnd.add(it)
                                }
                            }
                        }
                ){
                    AsyncImage(
                        model = endUri,
                        contentDescription = null,
                        contentScale = ContentScale.FillBounds,
                        modifier = Modifier.fillMaxSize()
                    )
                    Canvas(
                        modifier = Modifier.fillMaxSize()
                    ){
                        dotsEnd.forEachIndexed{index,offset->
                            drawCircle(
                                color = if(index == 0) Color.Red else if (index == 1) Color.Green else Color.Blue,
                                radius = 20f,
                                center = offset
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
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
            }
        }
    }
}
