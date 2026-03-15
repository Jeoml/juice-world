package com.juice.app

import android.app.Application
import com.juice.app.service.ApiService
import com.juice.app.service.LocationService
import org.osmdroid.config.Configuration

class JuiceApplication : Application() {

    lateinit var apiService: ApiService
        private set

    lateinit var locationService: LocationService
        private set

    override fun onCreate() {
        super.onCreate()

        // Configure osmdroid
        Configuration.getInstance().apply {
            userAgentValue = packageName
            osmdroidTileCache = cacheDir
        }

        apiService = ApiService()
        locationService = LocationService(this)
    }
}
