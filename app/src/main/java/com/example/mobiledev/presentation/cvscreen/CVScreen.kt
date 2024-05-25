package com.example.mobiledev.presentation.cvscreen

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.mobiledev.R
import com.example.mobiledev.presentation.algoritms.FaceRecognition
import com.example.mobiledev.presentation.algoritms.util.createAppDirectoryIfNotExists
import com.example.mobiledev.presentation.algoritms.util.saveToFile
import com.example.mobiledev.presentation.algoritms.util.toBitmap
import com.example.mobiledev.presentation.editorscreen.common.IconButton
import com.example.mobiledev.presentation.editorscreen.generateNewFile
import com.example.mobiledev.presentation.sidebar.common.SideBarItem
import kotlinx.coroutines.launch
import java.io.IOException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CVScreen(
    navController: NavController
) {
    val cvViewModel = viewModel<CVScreenViewModel>()
    val stateUri by cvViewModel.stateUriFlow.collectAsState()
    val context = LocalContext.current

    val pickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> cvViewModel.onStateUpdate(uri) }
    )
    var uriForCapturing: Uri = FileProvider.getUriForFile(
        context,
        context.applicationContext.packageName.toString() + ".provider",
        generateNewFile()
    )

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (success) {
                cvViewModel.onStateUpdate(uriForCapturing)
            }
        }
    )

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerContent = {
            ModalDrawerSheet {
                cvViewModel.getMenuItems().forEachIndexed { index, item ->
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
                        Text(text = stringResource(id = R.string.cv), color = Color.White)
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
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    AsyncImage(
                        model = stateUri,
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(500.dp)
                            .clip(RoundedCornerShape(20.dp))
                    )

                    AnimatedVisibility(visible = stateUri != null) {
                        Column(
                            modifier = Modifier
                                .animateContentSize()
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.End,
                            verticalArrangement = Arrangement.Top
                        ) {
                            IconButton(
                                onClick = {
                                    cvViewModel.onStateUpdate(
                                        FaceRecognition(
                                            readBytes(
                                                context,
                                                stateUri
                                            ), context.resources
                                        )
                                    )
                                },
                                icon = R.drawable.ic_accept
                            )
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            modifier = Modifier
                                .padding(horizontal = 8.dp),
                            icon = R.drawable.ic_pic,
                            onClick = {
                                pickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            isEnabled = true
                        )
                        Button(
                            modifier = Modifier
                                .padding(horizontal = 8.dp)
                                .border(5.dp, Color.Black, CircleShape)
                                .size(AssistChipDefaults.IconSize * 4)
                                .clip(RoundedCornerShape(1f)),
                            onClick = {
                                uriForCapturing = FileProvider.getUriForFile(
                                    context,
                                    context.applicationContext.packageName.toString() + ".provider",
                                    generateNewFile()
                                )
                                cameraLauncher.launch(uriForCapturing)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White,
                                Color.Black
                            )
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_camera),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(AssistChipDefaults.IconSize * 2)
                                    .align(Alignment.CenterVertically)
                            )
                        }
                        IconButton(
                            icon = R.drawable.ic_save,
                            onClick = {
                                val directory = createAppDirectoryIfNotExists()
                                Toast.makeText(
                                    context,
                                    "Файл сохранен в директорию " + directory.toString(),
                                    Toast.LENGTH_LONG
                                ).show()
                                saveToFile(
                                    toBitmap(
                                        com.example.mobiledev.presentation.editorscreen.readBytes(
                                            context,
                                            stateUri
                                        )
                                    ), "IMG", ".jpg", directory
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@Throws(IOException::class)
private fun readBytes(context: Context, uri: Uri?): ByteArray? =
    uri?.let { context.contentResolver.openInputStream(it)?.use { it.buffered().readBytes() } }