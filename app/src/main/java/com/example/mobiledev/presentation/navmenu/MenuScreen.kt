package com.example.mobiledev.presentation.navmenu

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
import com.example.mobiledev.R
import com.example.mobiledev.presentation.navmenu.common.Menu
import com.example.mobiledev.presentation.navmenu.common.menuElement
import com.example.mobiledev.presentation.navgraph.Route

@Composable
fun MenuScreen(
    navController:NavController,
    id:Int
){
    val menuitems = listOf(
        menuElement(0,R.drawable.ic_filter,R.string.filters),
        menuElement(1, R.drawable.ic_cv, R.string.cv),
        menuElement(2, R.drawable.ic_brokenline, R.string.vector),
        menuElement(3, R.drawable.ic_dots, R.string.biline),
        menuElement(4, R.drawable.ic_cube, R.string.cube)
    )

    var selected by remember {
        mutableIntStateOf(id)
    }

    Menu(
        items = menuitems,
        selected = selected,
        onItemClick = {idClick->
            navController.navigate(Route.FilterScreen.route + "/${idClick}")
            selected = idClick
        }
    )
}