package com.example.mobile_project

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.mobile_project.core.appwrite.AppwriteClientProvider
import com.example.mobile_project.ui.navigation.AppNavGraph
import com.example.mobile_project.ui.theme.Mobile_projectTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppwriteClientProvider.init(this)
        enableEdgeToEdge()
        setContent {
            Mobile_projectTheme {
                AppNavGraph()
            }
        }
    }
}
