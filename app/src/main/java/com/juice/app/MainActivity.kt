package com.juice.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.juice.app.navigation.JuiceNavGraph
import com.juice.app.ui.theme.JuiceTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JuiceTheme {
                JuiceNavGraph()
            }
        }
    }
}
