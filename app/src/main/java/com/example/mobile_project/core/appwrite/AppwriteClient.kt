package com.example.mobile_project.core.appwrite

import android.content.Context
import io.appwrite.Client
import io.appwrite.services.Account
import io.appwrite.services.Databases
import io.appwrite.services.Storage

object AppwriteClient {
    private lateinit var client: Client
    
    lateinit var account: Account
        private set
        
    lateinit var databases: Databases
        private set
        
    lateinit var storage: Storage
        private set

    fun init(context: Context) {
        client = Client(context)
            .setEndpoint(AppwriteConfig.ENDPOINT)
            .setProject(AppwriteConfig.PROJECT_ID)

        account = Account(client)
        databases = Databases(client)
        storage = Storage(client)
    }
}
