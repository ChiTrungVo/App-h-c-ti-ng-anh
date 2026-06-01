package com.example.mobile_project

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.mobile_project.core.appwrite.AppwriteClientProvider
import com.example.mobile_project.ui.navigation.AppNavGraph
import com.example.mobile_project.ui.theme.Mobile_projectTheme

class MainActivity : ComponentActivity() {
    private var incomingDeepLink by mutableStateOf<Uri?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppwriteClientProvider.init(this)
        incomingDeepLink = intent?.data?.takeIf { it.isMinLishDeepLink() }
        enableEdgeToEdge()
        setContent {
            Mobile_projectTheme {
                AppNavGraph(
                    incomingDeepLink = incomingDeepLink,
                    onIncomingDeepLinkConsumed = { incomingDeepLink = null }
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        incomingDeepLink = intent.data?.takeIf { it.isMinLishDeepLink() }
    }

    private fun Uri.isMinLishDeepLink(): Boolean {
        return host in setOf("verify-email", "reset-password") && (scheme == "https" || scheme == "minlish")
    }
}
