package com.pawfinder.app.data.remote.cloudinary

import android.content.Context
import com.cloudinary.android.MediaManager
import com.pawfinder.app.BuildConfig

object CloudinaryManager {

    private var isInitialized = false

    fun init(context: Context) {
        if (isInitialized) return

        val config = hashMapOf(
            "cloud_name" to BuildConfig.CLOUDINARY_CLOUD_NAME,
            "api_key" to BuildConfig.CLOUDINARY_API_KEY,
            "api_secret" to BuildConfig.CLOUDINARY_API_SECRET
        )

        MediaManager.init(context.applicationContext, config)
        isInitialized = true
    }
}