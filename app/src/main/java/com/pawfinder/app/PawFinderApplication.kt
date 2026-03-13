package com.pawfinder.app

import android.app.Application
import com.pawfinder.app.utils.CloudinaryManager

class PawFinderApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        CloudinaryManager.init(this)
    }
}