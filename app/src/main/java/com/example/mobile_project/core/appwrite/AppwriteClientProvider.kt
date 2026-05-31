package com.example.mobile_project.core.appwrite

import android.content.Context
import com.example.mobile_project.BuildConfig
import io.appwrite.Client
import io.appwrite.services.Account
import io.appwrite.services.Databases
import io.appwrite.services.Storage

object AppwriteClientProvider {
    lateinit var client: Client
        private set
    lateinit var account: Account
        private set
    lateinit var databases: Databases
        private set
    lateinit var storage: Storage
        private set

    val databaseId: String = BuildConfig.APPWRITE_DATABASE_ID
    val mediaBucketId: String = BuildConfig.APPWRITE_MEDIA_BUCKET_ID

    fun init(context: Context) {
        if (::client.isInitialized) return

        client = Client(context.applicationContext)
            .setEndpoint(BuildConfig.APPWRITE_ENDPOINT)
            .setProject(BuildConfig.APPWRITE_PROJECT_ID)

        account = Account(client)
        databases = Databases(client)
        storage = Storage(client)
    }
}
