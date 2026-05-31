package com.example.mobile_project

import android.app.Application
import com.example.mobile_project.core.appwrite.AppwriteClient
import com.example.mobile_project.core.appwrite.AppwriteClientProvider

class MinLishApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AppwriteClientProvider.init(this)
        AppwriteClient.init(this)
    }
}
