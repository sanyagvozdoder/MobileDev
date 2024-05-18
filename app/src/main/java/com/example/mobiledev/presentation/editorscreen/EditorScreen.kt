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
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.example.mobiledev.presentation.algoritms.Contrast
import com.example.mobiledev.presentation.algoritms.Grayscale
import com.example.mobiledev.presentation.algoritms.Negative
import com.example.mobiledev.presentation.algoritms.Rotate
import com.example.mobiledev.presentation.algoritms.Scaling
import com.example.mobiledev.presentation.editorscreen.common.IconButton
import com.example.mobiledev.presentation.editorscreen.common.SettingsItems
import com.example.mobiledev.presentation.editorscreen.common.SettingsTools
import com.example.mobiledev.presentation.editorscreen.common.Slider
import com.example.mobiledev.presentation.editorscreen.common.sliderElement
import com.example.mobiledev.presentation.navgraph.Route
import com.example.mobiledev.presentation.sidebar.common.SideBarItem
import com.example.mobiledev.presentation.sidebar.common.sideBarElement
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.IOException
import java.security.AccessController.getContext
import androidx.compose.runtime.Composable as Composable
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import com.example.mobiledev.presentation.algoritms.UnsharpMask
import com.example.mobiledev.presentation.algoritms.SeamCarving

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
                            model = stateUri.currentValue.value,
                            contentDescription = null,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(0.8f)
                        )

                        Spacer(Modifier.fillMaxHeight(0.2f))

                        AnimatedVisibility(
                            visible = sliderState
                        ) {
                            Slider(
                                modifier = Modifier.fillMaxSize(),
                                items = sliderElelements,
                                vmInst = editViewModel
                            )
                        }

                        AnimatedVisibility(visible = settingsState != -1) {
                            SettingsTools(
                                onAcceptClick = if(settingsState != -1 ) functionsAlghoritms[settingsState] else functionsAlghoritms[0],
                                sliders = if(settingsState != -1 ) settings[settingsState] else null,
                                editorScreenViewModel = editViewModel,
                                byteArray = readBytes(context,stateUri.currentValue.value)
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
    sideBarElement(4, R.drawable.ic_cube, R.string.cube, Route.CubeScreen.route)
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
    sliderElement(8, R.drawable.ic_spiral,R.string.mask),
)

val settings = listOf(
    SettingsItems(1, listOf("Угол поворота"), listOf<Pair<Int,Int>>(Pair(-180, 180))),
    SettingsItems(1, listOf("Коэфицент масштабирования"), listOf<Pair<Int,Int>>(Pair(50, 200))),
    SettingsItems(1, listOf("Коэфицент контраста"), listOf<Pair<Int,Int>>(Pair(-100, 100))),
    SettingsItems(0, listOf(), listOf<Pair<Int,Int>>()),
    SettingsItems(0, listOf(), listOf<Pair<Int,Int>>()),
    SettingsItems(0, listOf(), listOf<Pair<Int,Int>>()),
    SettingsItems(0, listOf(), listOf<Pair<Int,Int>>()),
    SettingsItems(1, listOf("Итераций",), listOf<Pair<Int,Int>>(Pair(1, 100))),
    SettingsItems(3, listOf("Порог", "Радиус", "Количество"), listOf<Pair<Int,Int>>(Pair(0, 255), Pair(0, 100), Pair(0, 5)))
)

val functionsAlghoritms = listOf<(ByteArray?, EditorScreenViewModel, List<Int>) -> Unit>(
    ::Rotate,
    ::Scaling,
    ::Contrast,
    ::Grayscale,
    ::Negative,
    ::Scaling,
    ::Scaling,
    ::SeamCarving,
    ::UnsharpMask,
)

@Throws(IOException::class)
private fun readBytes(context: Context, uri: Uri?): ByteArray? =
    uri?.let { context.contentResolver.openInputStream(it)?.use { it.buffered().readBytes() } }

