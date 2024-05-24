package com.example.mobiledev.data.route

sealed class Route(
    val route: String
) {
    object FilterScreen: Route(route="filterScreen")
    object CVScreen: Route(route = "cvScreen")
    object VectorScreen: Route(route = "vectorScreen")
    object AfineScreen: Route(route = "afineScreen")
    object CubeScreen: Route(route = "CubeScreen")
    object RetouchScreen:Route(route = "RetouchScreen")
}