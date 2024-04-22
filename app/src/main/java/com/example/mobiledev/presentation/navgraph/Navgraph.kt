package com.example.mobiledev.presentation.navgraph

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.mobiledev.presentation.editorscreen.EditorScreen
import com.example.mobiledev.presentation.navmenu.MenuScreen

@Composable
fun NavGraph(){
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Route.MenuScreen.route + "/${0}"
    ){
        composable(route = Route.MenuScreen.route + "/{number}", arguments = listOf(navArgument("number"){
            type = NavType.IntType
        })){
            MenuScreen(navController,it.arguments?.getInt("number")?:0)
        }
        composable(route = Route.FilterScreen.route + "/{number}", arguments = listOf(navArgument("number"){
            type = NavType.IntType
        })){
            EditorScreen(navController,it.arguments?.getInt("number")?:0)
        }
    }
}