package com.example.mobiledev.presentation.vectorscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobiledev.data.sidebarmenu.SideBarElement
import com.example.mobiledev.data.sidebarmenu.menuitems
import com.example.mobiledev.presentation.algoritms.util.SplineMode
import com.example.mobiledev.presentation.algoritms.util.VectorScreenMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class VectorScreenViewModel : ViewModel() {
    private val _screenModeState = MutableStateFlow<VectorScreenMode>(VectorScreenMode.DRAW)

    val screenModeState: StateFlow<VectorScreenMode>
        get() = _screenModeState

    fun onScreenModeUpdate(newMode: VectorScreenMode) {
        viewModelScope.launch {
            _screenModeState.emit(newMode)
        }
    }

    private val _splineModeState = MutableStateFlow<SplineMode>(SplineMode.LINE)

    val splineModeState: StateFlow<SplineMode>
        get() = _splineModeState

    fun onSplineModeUpdate(newMode: SplineMode) {
        viewModelScope.launch {
            _splineModeState.emit(newMode)
        }
    }

    fun getMenuItems(): List<SideBarElement> {
        return menuitems
    }
}