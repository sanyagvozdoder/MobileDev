package com.example.mobiledev.presentation.vectorscreen

import androidx.lifecycle.ViewModel
import com.example.mobiledev.data.sidebarmenu.SideBarElement
import com.example.mobiledev.data.sidebarmenu.menuitems

class VectorScreenViewModel:ViewModel() {
    fun getMenuItems():List<SideBarElement>{
        return menuitems
    }
}