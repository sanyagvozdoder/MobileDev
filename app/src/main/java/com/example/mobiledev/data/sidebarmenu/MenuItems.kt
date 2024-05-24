package com.example.mobiledev.data.sidebarmenu

import com.example.mobiledev.R
import com.example.mobiledev.data.route.Route

val menuitems = listOf(
    SideBarElement(0, R.drawable.ic_filter, R.string.filters, Route.FilterScreen.route),
    SideBarElement(1, R.drawable.ic_cv, R.string.cv, Route.CVScreen.route),
    SideBarElement(2, R.drawable.ic_brokenline, R.string.vector, Route.VectorScreen.route),
    SideBarElement(3, R.drawable.ic_dots, R.string.biline, Route.BilineScreen.route),
    SideBarElement(4, R.drawable.ic_cube, R.string.cube, Route.CubeScreen.route),
    SideBarElement(5, R.drawable.ic_retouch, R.string.retouching, Route.RetouchScreen.route)
)