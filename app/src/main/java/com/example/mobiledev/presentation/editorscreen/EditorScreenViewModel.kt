package com.example.mobiledev.presentation.editorscreen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.ActivityNavigatorExtras
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EditorScreenViewModel:ViewModel() {
    private val _stateUriFlow = MutableStateFlow<Uri?>(null)

    val stateUriFlow: StateFlow<Uri?>
        get() = _stateUriFlow

    fun onStateUpdate(newImage:Uri?){
        viewModelScope.launch{
            _stateUriFlow.emit(newImage)
        }
    }

    private val _isSliderVisible = MutableStateFlow<Boolean>(false)

    val isSliderVisible:StateFlow<Boolean>
        get() = _isSliderVisible

    fun onSliderStateUpdate(state:Boolean){
        viewModelScope.launch{
            _isSliderVisible.emit(state)
        }
    }

    private val _settingsState = MutableStateFlow<Int>(-1)

    val settingsState:StateFlow<Int>
        get() = _settingsState

    fun onSettingsStateUpdate(state:Int){
        viewModelScope.launch{
            _settingsState.emit(state)
        }
    }
}
