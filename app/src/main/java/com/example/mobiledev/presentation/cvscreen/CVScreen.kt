package com.example.mobiledev.presentation.cvscreen

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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.mobiledev.R
import com.example.mobiledev.presentation.algoritms.FaceRecognition
import com.example.mobiledev.presentation.editorscreen.common.IconButton
import com.example.mobiledev.presentation.editorscreen.common.SettingsTools
import com.example.mobiledev.presentation.editorscreen.common.Slider
import com.example.mobiledev.presentation.sidebar.common.SideBarItem
import kotlinx.coroutines.launch
import java.io.IOException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CVScreen(
    navController: NavController
){
    val cvViewModel = viewModel<CVScreenViewModel>()
    val stateUri by cvViewModel.stateUriFlow.collectAsState()
    val context = LocalContext.current

    val pickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = {uri-> cvViewModel.onStateUpdate(uri)}
    )

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerContent = {
            ModalDrawerSheet {
                cvViewModel.getMenuItems().forEachIndexed{ index, item->
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
                        Text(text = "Распознавание лиц", color = Color.White)
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
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                )
            },
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it),
            ) {
                Box(
                    modifier = Modifier.fillMaxSize()
                ){
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
                    )
                    Button(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(horizontal = 5.dp, vertical = 5.dp),
                        onClick = {
                            cvViewModel.onStateUpdate(FaceRecognition(readBytes(context, stateUri), context.resources))
                        }
                    ){
                        Text(text = "Launch")
                    }
                }
            }
        }
    }
}

@Throws(IOException::class)
private fun readBytes(context: Context, uri: Uri?): ByteArray? =
    uri?.let { context.contentResolver.openInputStream(it)?.use { it.buffered().readBytes() }}