package com.example.mobiledev.presentation.navgraph

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mobiledev.data.route.Route
import com.example.mobiledev.presentation.afinescreen.AfineScreen
import com.example.mobiledev.presentation.cubescreen.CubeScreen
import com.example.mobiledev.presentation.cvscreen.CVScreen
import com.example.mobiledev.presentation.editorscreen.EditorScreen
import com.example.mobiledev.presentation.retouchscreen.RetouchScreen
import com.example.mobiledev.presentation.vectorscreen.VectorScreen

@Composable
fun NavGraph(){
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Route.FilterScreen.route
    ){
        composable(route = Route.FilterScreen.route){
            EditorScreen(navController)
        }
        composable(route = Route.VectorScreen.route){
            VectorScreen(navController)
        }
        composable(route = Route.AfineScreen.route){
            AfineScreen(navController = navController)
        }
        composable(route = Route.CVScreen.route){
            CVScreen(navController = navController)
        }
        composable(route = Route.CubeScreen.route){
            CubeScreen(navController = navController)
        }
        composable(route = Route.RetouchScreen.route){
            RetouchScreen(navController = navController)
        }
    }
}