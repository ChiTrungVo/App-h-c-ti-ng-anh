package com.example.mobile_project

import android.app.Application
import com.example.mobile_project.core.appwrite.AppwriteClient

class MinLishApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize Appwrite Client
        AppwriteClient.init(this)
    }
}
