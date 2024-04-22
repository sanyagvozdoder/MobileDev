package com.example.mobiledev.presentation.navgraph

sealed class Route(
    val route: String
) {
    object AppNavigation:Route(route="appNavigation")

    object MenuScreen:Route(route="menuScreen")

    object FilterScreen:Route(route="filterScreen")

}