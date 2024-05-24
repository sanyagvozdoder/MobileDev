package com.example.mobiledev.presentation.cvscreen

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobiledev.data.sidebarmenu.SideBarElement
import com.example.mobiledev.data.sidebarmenu.menuitems
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CVScreenViewModel:ViewModel() {
    private val _stateUriFlow = MutableStateFlow<Uri?>(null)

    val stateUriFlow: StateFlow<Uri?>
        get() = _stateUriFlow

    fun onStateUpdate(newImage: Uri?){
        viewModelScope.launch{
            _stateUriFlow.emit(newImage)
        }
    }

    fun getMenuItems():List<SideBarElement>{
        return menuitems
    }
}

