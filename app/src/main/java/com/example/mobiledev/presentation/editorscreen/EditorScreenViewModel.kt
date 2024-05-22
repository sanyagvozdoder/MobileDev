package com.example.mobiledev.presentation.editorscreen

import android.net.Uri
import android.os.Environment
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.core.content.FileProvider
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.ActivityNavigatorExtras
import com.example.mobiledev.presentation.algoritms.util.generateUri
import com.example.mobiledev.presentation.undoredostates.StateSaver
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File

class EditorScreenViewModel:ViewModel() {
    private val _stateUri = MutableStateFlow<StateSaver>(StateSaver(null))

    val stateUriFlow: StateFlow<StateSaver>
        get() = _stateUri

    fun onStateUpdate(newImage:Uri?){
        _stateUri.value.update(newImage)
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

fun generateNewFile():File{
    val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
    val file = File.createTempFile("newImage", ".jpg", downloadsDir)
    return file
}
