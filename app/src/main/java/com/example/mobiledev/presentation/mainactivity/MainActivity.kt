package com.example.mobiledev.presentation.mainactivity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.mobiledev.presentation.algoritms.util.cleanTmpDirectory
import com.example.mobiledev.presentation.navgraph.NavGraph
import com.example.mobiledev.ui.theme.MobileDevTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContent{
            MobileDevTheme {
                Box(
                    modifier = Modifier.fillMaxSize()
                ){
                    NavGraph()
                }
            }
        }
    }

    override fun onStop() {
        cleanTmpDirectory()
        super.onStop()
    }
}

@Preview
@Composable
fun aaa(){
    Box(
        modifier = Modifier.fillMaxSize()
    ){
        NavGraph()
    }
}


