package com.example.mobiledev.presentation.retouchscreen;

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobiledev.data.sidebarmenu.SideBarElement
import com.example.mobiledev.data.sidebarmenu.menuitems
import com.example.mobiledev.presentation.undoredostates.StateSaver
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RetouchScreenViewModel:ViewModel() {
        private val _stateUri = MutableStateFlow<StateSaver>(StateSaver(null))

        val stateUriFlow: StateFlow<StateSaver>
                get() = _stateUri

        fun onStateUpdate(newImage:Uri?){
                _stateUri.value.update(newImage)
        }

        private val _buttonLock = MutableStateFlow<Boolean>(true)

        val buttonLock:StateFlow<Boolean>
                get() = _buttonLock

        fun onButtonLockUpdate(state:Boolean){
                viewModelScope.launch{
                        _buttonLock.emit(state)
                }
        }

        fun getMenuItems():List<SideBarElement>{
                return menuitems
        }
}