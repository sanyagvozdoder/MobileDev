package com.example.mobiledev.presentation.editorscreen

import android.content.Context
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.core.content.FileProvider
import com.example.mobiledev.presentation.algoritms.SeamCarving
import com.example.mobiledev.presentation.algoritms.UnsharpMask
import java.security.AccessController.getContext
import androidx.compose.runtime.Composable as Composable
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.mobiledev.presentation.algoritms.SeamCarving
import com.example.mobiledev.presentation.algoritms.UnsharpMask
import org.opencv.BuildConfig

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreen(
    navController: NavController,
){
    val editViewModel = viewModel<EditorScreenViewModel>()
    val stateUri by editViewModel.stateUriFlow.collectAsState()
    val sliderState by editViewModel.isSliderVisible.collectAsState()
    val settingsState by editViewModel.settingsState.collectAsState()

    val context = LocalContext.current

    val pickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = {uri-> editViewModel.onStateUpdate(uri)}
    )

    var uriForCapturing:Uri = FileProvider.getUriForFile(
        context,
        context.applicationContext.packageName.toString() + ".provider",
        generateNewFile()
    )

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = {success->
            if(success){
                editViewModel.onStateUpdate(uriForCapturing)
            }
        }
    )

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerContent = {
            ModalDrawerSheet {
                menuitems.forEachIndexed{index,item->
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
                        Text(text = "Фильтры", color = Color.White)
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
                    .padding(it)
                    .fillMaxSize()
            ) {
                Row(
                    modifier = Modifier.fillMaxHeight(0.05f)
                ) {
                    Button(
                        onClick = {
                            stateUri.undo()
                        },
                        enabled = stateUri.undoSize() >= 2
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_undo),
                            contentDescription = null,
                            modifier = Modifier.size(AssistChipDefaults.IconSize)
                        )
                    }
                    Button(
                        onClick = {
                            stateUri.redo()
                        },
                        enabled = stateUri.redoSize() >= 2
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_redo),
                            contentDescription = null,
                            modifier = Modifier.size(AssistChipDefaults.IconSize)
                        )
                    }
                }

                Column(
                    modifier = Modifier.fillMaxSize(0.9f)
                ){
                    AsyncImage(
                        model = stateUri.currentValue.value,
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(0.8f)
                    )

                    Spacer(modifier = Modifier.height(3.dp))

                    AnimatedVisibility(
                        visible = sliderState,
                        modifier = Modifier
                            .padding(vertical = 25.dp)
                    ) {
                        Slider(
                            modifier = Modifier.fillMaxSize(),
                            items = sliderElelements,
                            vmInst = editViewModel
                        )
                    }

                    AnimatedVisibility(visible = settingsState != -1, modifier = Modifier
                        .fillMaxHeight()
                        .padding(vertical = 20.dp)) {
                        SettingsTools(
                            onAcceptClick = if(settingsState != -1 ) functionsAlghoritms[settingsState] else functionsAlghoritms[0],
                            sliders = if(settingsState != -1 ) settings[settingsState] else null,
                            editorScreenViewModel = editViewModel,
                            byteArray = readBytes(context,stateUri.currentValue.value)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(5.dp))

                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        modifier = Modifier
                            .padding(horizontal = 5.dp, vertical = 5.dp),
                        icon = R.drawable.ic_pic,
                        onClick = {
                            pickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                            editViewModel.onSliderStateUpdate(true)
                        },
                        isEnabled = true
                    )
                    IconButton(
                        modifier = Modifier
                            .padding(horizontal = 5.dp, vertical = 5.dp),
                        icon = R.drawable.ic_pic,
                        onClick = {
                            uriForCapturing = FileProvider.getUriForFile(
                                context,
                                context.applicationContext.packageName.toString() + ".provider",
                                generateNewFile()
                            )
                            cameraLauncher.launch(uriForCapturing)
                            editViewModel.onSliderStateUpdate(true)
                        },
                        isEnabled = true
                    )
                }
            }
        }
    }
}

val menuitems = listOf(
    sideBarElement(0, R.drawable.ic_filter, R.string.filters,Route.FilterScreen.route),
    sideBarElement(1, R.drawable.ic_cv, R.string.cv,Route.CVScreen.route),
    sideBarElement(2, R.drawable.ic_brokenline, R.string.vector,Route.VectorScreen.route),
    sideBarElement(3, R.drawable.ic_dots, R.string.biline, Route.BilineScreen.route),
    sideBarElement(4, R.drawable.ic_cube, R.string.cube, Route.CubeScreen.route),
    sideBarElement(5, R.drawable.ic_retouch, R.string.retouching, Route.RetouchScreen.route)
)

val sliderElelements = listOf(
    sliderElement(0, R.drawable.ic_rotate,R.string.rotate),
    sliderElement(1, R.drawable.ic_scale,R.string.scale),
    sliderElement(2, R.drawable.ic_filter,R.string.filters),
    sliderElement(3, R.drawable.ic_filter,R.string.filters),
    sliderElement(4, R.drawable.ic_filter,R.string.filters),
    sliderElement(5, R.drawable.ic_retouch,R.string.retouching),
    sliderElement(6, R.drawable.ic_spiral,R.string.mask),
    sliderElement(7, R.drawable.ic_filter,R.string.filters), // жмых
)

val settings = listOf(
    SettingsItems(1, listOf("Угол"), listOf<Pair<Int,Int>>(Pair(-180, 180))),
    SettingsItems(1, listOf("Коэфицент масштабирования"), listOf<Pair<Int,Int>>(Pair(50, 200))),
    SettingsItems(1, listOf("Коэфицент контраста"), listOf<Pair<Int,Int>>(Pair(-100, 100))),
    SettingsItems(0, listOf(), listOf<Pair<Int,Int>>()),
    SettingsItems(0, listOf(), listOf<Pair<Int,Int>>()),
    SettingsItems(0, listOf(), listOf<Pair<Int,Int>>()),
    SettingsItems(3, listOf("Порог", "Радиус", "Количество"),
        listOf<Pair<Int,Int>>(Pair(0, 255), Pair(0, 100), Pair(0, 50))),
    SettingsItems(1, listOf("Итераций",), listOf<Pair<Int,Int>>(Pair(1, 100))),
)

val functionsAlghoritms = listOf<(ByteArray?, EditorScreenViewModel, List<Int>) -> Unit>(
    ::Rotate,
    ::Scaling,
    ::Contrast,
    ::Grayscale,
    ::Negative,
    ::Scaling,
    ::UnsharpMask,
    ::SeamCarving
)

@Throws(IOException::class)
fun readBytes(context: Context, uri: Uri?): ByteArray? =
    uri?.let { context.contentResolver.openInputStream(it)?.use { it.buffered().readBytes() } }


