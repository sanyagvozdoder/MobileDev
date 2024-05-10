package com.example.mobiledev.presentation.navgraph

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.mobiledev.presentation.bilinescreen.BilineScreen
import com.example.mobiledev.presentation.cvscreen.CVScreen
import com.example.mobiledev.presentation.editorscreen.EditorScreen
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
        composable(route = Route.BilineScreen.route){
            BilineScreen(navController = navController)
        }
        composable(route = Route.CVScreen.route){
            CVScreen(navController = navController)
        }
    }
}