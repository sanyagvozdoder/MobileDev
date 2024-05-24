package com.example.mobiledev.presentation.editorscreen


import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobiledev.data.settingsitems.SettingsItems
import com.example.mobiledev.data.settingsitems.settingsItemsList
import com.example.mobiledev.data.sidebarmenu.SideBarElement
import com.example.mobiledev.data.sidebarmenu.menuitems
import com.example.mobiledev.data.sliderelements.SliderElement
import com.example.mobiledev.data.sliderelements.sliderElelements
import com.example.mobiledev.presentation.algoritms.Blur
import com.example.mobiledev.presentation.algoritms.Contrast
import com.example.mobiledev.presentation.algoritms.Grayscale
import com.example.mobiledev.presentation.algoritms.Negative
import com.example.mobiledev.presentation.algoritms.Rotate
import com.example.mobiledev.presentation.algoritms.Scaling
import com.example.mobiledev.presentation.algoritms.SeamCarving
import com.example.mobiledev.presentation.algoritms.UnsharpMask
import com.example.mobiledev.presentation.algoritms.util.getTmpDirectory
import com.example.mobiledev.presentation.undoredostates.StateSaver
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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

    fun getMenuItems():List<SideBarElement>{
        return menuitems
    }

    fun getSliderElements():List<SliderElement>{
        return sliderElelements
    }

    fun getSettingsItems():List<SettingsItems>{
        return settingsItemsList
    }

    fun getFunctionsList() : List<(ByteArray?, (Uri?)->Unit, List<Int>) -> Unit>{
        return listOf<(ByteArray?, (Uri?)->Unit, List<Int>) -> Unit>(
            ::Rotate,
            ::Scaling,
            ::Contrast,
            ::Grayscale,
            ::Negative,
            ::Blur,
            ::SeamCarving,
            ::UnsharpMask
        )
    }
}

fun generateNewFile():File{
    val file = File.createTempFile("newImage", ".jpg", getTmpDirectory())
    return file
}
