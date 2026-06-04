package com.example.mobile_project

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import com.example.mobile_project.core.appwrite.AppwriteClientProvider
import com.example.mobile_project.ui.navigation.AppNavGraph
import com.example.mobile_project.ui.theme.Mobile_projectTheme
import android.Manifest
import android.content.pm.PackageManager

class MainActivity : ComponentActivity() {
    private var incomingDeepLink by mutableStateOf<Uri?>(null)
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { /* không cần xử lý */ }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppwriteClientProvider.init(this)
        incomingDeepLink = intent?.data?.takeIf { it.isMinLishDeepLink() }
        enableEdgeToEdge()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
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
