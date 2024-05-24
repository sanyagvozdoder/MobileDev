package com.example.mobiledev.presentation.cubescreen

import androidx.lifecycle.ViewModel
import com.example.mobiledev.data.sidebarmenu.SideBarElement
import com.example.mobiledev.data.sidebarmenu.menuitems

class CubeScreenViewModel:ViewModel() {
    fun getMenuItems():List<SideBarElement>{
        return menuitems
    }
}