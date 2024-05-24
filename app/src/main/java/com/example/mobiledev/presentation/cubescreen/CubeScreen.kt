package com.example.mobiledev.presentation.cubescreen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mobiledev.R
import com.example.mobiledev.presentation.algoritms.util.Camera
import com.example.mobiledev.presentation.algoritms.util.Face
import com.example.mobiledev.presentation.algoritms.util.Vertex
import com.example.mobiledev.presentation.algoritms.util.moveZ
import com.example.mobiledev.presentation.algoritms.util.project
import com.example.mobiledev.presentation.algoritms.util.rotateXYZ
import com.example.mobiledev.presentation.editorscreen.common.IconButton
import com.example.mobiledev.presentation.sidebar.common.SideBarItem
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CubeScreen(
    navController: NavController
){
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val viewModel = viewModel<CubeScreenViewModel>()

    val angleX = remember {
        mutableStateOf(0)
    }

    val angleY = remember {
        mutableStateOf(0)
    }

    val angleZ = remember {
        mutableStateOf(0)
    }

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
                        Text(text = "3D кубик", color = Color.White)
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
                ) {
                    //https://stackoverflow.com/questions/58772212/what-are-the-correct-vertices-and-indices-for-a-cube-using-this-mesh-function
                    val verts = arrayListOf(
                        Vertex(-1f, -1f, 1f), Vertex(1f, -1f, 1f),
                        Vertex(-1f, 1f, 1f), Vertex(1f, 1f, 1f),
                        Vertex(-1f, -1f, -1f), Vertex(1f, -1f, -1f),
                        Vertex(-1f, 1f, -1f), Vertex(1f, 1f, -1f)
                    )

                    val faces = arrayListOf(
                        Face(2, 6, 7, Color.Yellow), Face(2, 3, 7, Color.Yellow), // top
                        Face(0, 4, 5, Color.Yellow), Face(0, 1, 5, Color.Yellow), // bottom
                        Face(0, 2, 6, Color.Red), Face(0, 4, 6, Color.Red), // left
                        Face(1, 3, 7, Color.Red), Face(1, 5, 7, Color.Red), // right
                        Face(0, 2, 3, Color.Green), Face(0, 1, 3, Color.Green), // front
                        Face(4, 6, 7, Color.Green), Face(4, 5, 7, Color.Green), // back
                    )
                    /*
                    val verts = arrayListOf(
                        Vertex(0f, 0f, 0f), Vertex(1f, 0f, 0f),
                        Vertex(0f, 1f, 0f), Vertex(0f, 0f, 1f),
                    )

                    val edges = arrayListOf(
                        Edge(0, 1), Edge(0, 2), Edge(0, 3),
                        Edge(1, 2), Edge(1, 3), Edge(2, 3),
                    )*/

                    val x = Math.toRadians(angleX.value.toDouble())
                    val y = Math.toRadians(angleY.value.toDouble())
                    val z = Math.toRadians(angleZ.value.toDouble())
                    val scale = 500.0
                    val move = 3f

                    val cam = Camera(size, 1.0)

                    // алгоритм художника (Z-сортировка)
                    val sortedFaces = faces.sortedBy { face ->
                        val p0 = rotateXYZ(verts[face.a], x, y, z)
                        val p1 = rotateXYZ(verts[face.b], x, y, z)
                        val p2 = rotateXYZ(verts[face.c], x, y, z)
                        val center1 = Vertex(
                            (p0.x + p1.x) / 2,
                            (p0.y + p1.y) / 2,
                            (p0.z + p1.z) / 2
                        )
                        val faceCenter = Vertex(
                            (center1.x + p2.x) / 2,
                            (center1.y + p2.y) / 2,
                            (center1.z + p2.z) / 2
                        )

                        -(faceCenter.z)
                        // якобы дистанция до камеры
                        // (в общем случае - длина вектора от камеры к faceCenter)
                    }

                    sortedFaces.forEach { face ->
                        val p0 = rotateXYZ(verts[face.a], x, y, z)
                        val p1 = rotateXYZ(verts[face.b], x, y, z)
                        val p2 = rotateXYZ(verts[face.c], x, y, z)
                        val proj0 = project(cam, moveZ(p0, move), scale)
                        val proj1 = project(cam, moveZ(p1, move), scale)
                        val proj2 = project(cam, moveZ(p2, move), scale)
                        val triPath = Path().apply{
                            moveTo(proj0.x, proj0.y)
                            lineTo(proj1.x, proj1.y)
                            lineTo(proj2.x, proj2.y)
                            lineTo(proj0.x, proj0.y)
                        }
                        drawPath(triPath, face.color)
                        //drawPoints(listOf(proj0, proj1, proj2), PointMode.Lines, Color.Black, strokeWidth = 2f)
                    }

                    //DrawSpline(dots, mode, selectedDot, selectionMode, splineMode)
                    //drawPoints(dots, pointMode = PointMode.Polygon, Color.Black)
                }
                Column {

                    Row {
                        Text(text = "X: " + angleX.value.toString(),
                            modifier = Modifier.fillMaxWidth(0.2f))
                        androidx.compose.material3.Slider(
                            value = angleX.value.toFloat(),
                            onValueChange = { newValue ->
                                angleX.value = newValue.toInt()
                            },
                            valueRange = -180f..180f,
                            modifier = Modifier
                                .fillMaxWidth()
                        )
                    }
                    Row {
                        Text(text = "Y: " + angleY.value.toString(),
                            modifier = Modifier.fillMaxWidth(0.2f))
                        androidx.compose.material3.Slider(
                            value = angleY.value.toFloat(),
                            onValueChange = {newValue->
                                angleY.value = newValue.toInt()
                            },
                            valueRange = -180f..180f,
                            modifier = Modifier
                                .fillMaxWidth()
                        )
                    }
                    Row {
                        Text(text = "Z: " + angleZ.value.toString(),
                            modifier = Modifier.fillMaxWidth(0.2f))
                        androidx.compose.material3.Slider(
                            value = angleZ.value.toFloat(),
                            onValueChange = {newValue->
                                angleZ.value = newValue.toInt()
                            },
                            valueRange = -180f..180f,
                            modifier = Modifier
                                .fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}
