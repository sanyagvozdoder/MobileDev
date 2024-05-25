package com.example.mobiledev.presentation.retouchscreen

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.mobiledev.R
import com.example.mobiledev.data.sidebarmenu.menuitems
import com.example.mobiledev.presentation.algoritms.applyRetouch
import com.example.mobiledev.presentation.algoritms.util.createAppDirectoryIfNotExists
import com.example.mobiledev.presentation.algoritms.util.saveToFile
import com.example.mobiledev.presentation.algoritms.util.toBitmap
import com.example.mobiledev.presentation.editorscreen.common.IconButton
import com.example.mobiledev.presentation.editorscreen.generateNewFile
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

    var strength by remember {
        mutableStateOf(50f)
    }

    var workSpaceSize by remember { mutableStateOf(IntSize.Zero) }

    val context = LocalContext.current

    var uriForCapturing: Uri = FileProvider.getUriForFile(
        context,
        context.applicationContext.packageName.toString() + ".provider",
        generateNewFile()
    )

    val pickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri->
            viewModel.onStateUpdate(uri)
        }
    )

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = {success->
            if(success){
                viewModel.onStateUpdate(uriForCapturing)
            }
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
                        Text(text = stringResource(id = R.string.retouching), color = Color.White)
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
            ){
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(
                        modifier = Modifier
                            .weight(2f)
                            .fillMaxHeight()
                            .fillMaxWidth(0.9f)
                            .align(Alignment.CenterHorizontally)
                            .pointerInput(Unit) {
                                detectTapGestures {
                                    if(stateUri.currentValue.value != null){
                                        dots.add(it)
                                    }
                                }
                            }
                    ){
                        AsyncImage(
                            model = stateUri.currentValue.value,
                            contentDescription = null,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.fillMaxSize()
                        )
                        Canvas(
                            modifier = Modifier
                                .fillMaxSize()
                                .alpha(0.7f)
                                .onGloballyPositioned { coordinates ->
                                    workSpaceSize = coordinates.size
                                }
                        ){
                            dots.forEach{ dot ->
                                drawCircle(
                                    color = Color.Black,
                                    radius = currentRadius.toFloat(),
                                    center = dot
                                )
                            }
                        }
                    }
                    AnimatedVisibility(visible = stateUri.currentValue.value != null, modifier = Modifier.padding(vertical = 16.dp)) {
                        Column(modifier = Modifier.animateContentSize()){
                            IconButton(
                                modifier = Modifier.align(Alignment.End),
                                onClick = {
                                    applyRetouch(readBytes(context, stateUri.currentValue.value), strength,
                                        { uri ->
                                            viewModel.onStateUpdate(uri)
                                            dots.clear()
                                        }
                                        , dots, currentRadius, workSpaceSize)
                                },
                                icon = R.drawable.ic_accept
                            )
                            Text(text = stringResource(id = R.string.radius) + ": " + currentRadius.toString(), modifier = Modifier
                                .align(Alignment.CenterHorizontally))
                            androidx.compose.material3.Slider(
                                value = currentRadius.toFloat(),
                                onValueChange = {newValue->
                                    currentRadius = newValue.toInt()
                                },
                                valueRange = 1f..200f,
                                modifier = Modifier
                                    .fillMaxWidth()
                            )
                            Text(text = stringResource(id = R.string.strength)+ "(%): " + strength.toInt().toString(),modifier = Modifier.align(Alignment.CenterHorizontally))
                            androidx.compose.material3.Slider(
                                value = strength,
                                onValueChange = {newValue->
                                    strength = newValue
                                },
                                valueRange = 0f..100f,
                                modifier = Modifier
                                    .fillMaxWidth()
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
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White, Color.Black)
                        ){
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
                                Toast.makeText(context,"Файл сохранен в директорию " + directory.toString(), Toast.LENGTH_LONG).show()
                                saveToFile(toBitmap(readBytes(context,stateUri.currentValue.value)), "IMG", ".jpg", directory)
                            }
                        )
                    }
                }
            }
        }
    }
}
