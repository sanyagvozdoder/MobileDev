package com.example.mobiledev.presentation.afinescreen

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobiledev.data.sidebarmenu.SideBarElement
import com.example.mobiledev.data.sidebarmenu.menuitems
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AfineScreenViewModel:ViewModel() {
    private val _startUriFlow = MutableStateFlow<Uri?>(null)

    val startUriFlow: StateFlow<Uri?>
        get() = _startUriFlow

    fun onStartUpdate(newImage: Uri?){
        viewModelScope.launch{
            _startUriFlow.emit(newImage)
        }
    }

    private val _endUriFlow = MutableStateFlow<Uri?>(null)

    val endUriFlow: StateFlow<Uri?>
        get() = _endUriFlow

    fun onEndUpdate(newImage: Uri?){
        viewModelScope.launch{
            _endUriFlow.emit(newImage)
        }
    }

    fun getMenuItems():List<SideBarElement>{
        return menuitems
    }
}