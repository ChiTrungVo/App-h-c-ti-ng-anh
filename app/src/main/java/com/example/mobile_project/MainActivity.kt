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
    private var verificationDeepLink by mutableStateOf<Uri?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppwriteClientProvider.init(this)
        verificationDeepLink = intent?.data?.takeIf { it.isEmailVerificationLink() }
        enableEdgeToEdge()
        setContent {
            Mobile_projectTheme {
                AppNavGraph(
                    emailVerificationDeepLink = verificationDeepLink,
                    onEmailVerificationDeepLinkConsumed = { verificationDeepLink = null }
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        verificationDeepLink = intent.data?.takeIf { it.isEmailVerificationLink() }
    }

    private fun Uri.isEmailVerificationLink(): Boolean {
        return scheme == "minlish" && host == "verify-email"
    }
}
