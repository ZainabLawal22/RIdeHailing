package com.example.ridehailing

import android.app.Application
import android.util.Log
import dagger.hilt.android.HiltAndroidApp

/**
 * Application class for RideHail app
 * Annotated with @HiltAndroidApp to enable Hilt dependency injection
 */
@HiltAndroidApp
class RideHailingApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Log.d("RideApp", "Application onCreate - Hilt initialized")
    }
}