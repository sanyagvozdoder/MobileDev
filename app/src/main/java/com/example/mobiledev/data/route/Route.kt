package com.example.mobiledev.data.route

sealed class Route(
    val route: String
) {
    object FilterScreen: Route(route="filterScreen")
    object CVScreen: Route(route = "cvScreen")
    object VectorScreen: Route(route = "vectorScreen")
    object BilineScreen: Route(route = "bilineScreen")
    object CubeScreen: Route(route = "CubeScreen")

}