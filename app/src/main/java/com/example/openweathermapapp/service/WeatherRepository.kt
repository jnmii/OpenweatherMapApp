package com.example.openweathermapapp.service

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.Application
import android.location.Location
import android.os.Looper
import androidx.annotation.RequiresPermission
import com.example.openweathermapapp.service.dto.CurrentWeather
import com.example.openweathermapapp.service.dto.FullWeather
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class WeatherRepository @Inject constructor(
    private val service: OpenWeatherService,
    private val application: Application
) {

    @RequiresPermission(ACCESS_FINE_LOCATION)
    fun getCurrentWeather(): Flow<CurrentWeather> {
        return locationFlow().map {
            service.getCurrentWeather(it.latitude, it.longitude, "c6b139b520c92278e251fcfcc4f01648")
                .body()
        }.filterNotNull()
    }

    @RequiresPermission(ACCESS_FINE_LOCATION)
    fun getFiveDayForecast(): Flow<List<FullWeather.Daily>> {
        return locationFlow().map {
            service.getFullWeather(it.latitude, it.longitude, "c6b139b520c92278e251fcfcc4f01648")
                .body()
        }.map {
            it?.daily?.drop(1)?.take(5)
        }.filterNotNull()
    }

    @RequiresPermission(ACCESS_FINE_LOCATION)
    private fun locationFlow() = channelFlow<Location> {
        val client = LocationServices.getFusedLocationProviderClient(application)
        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { trySend(it) }
            }
        }
        val request = LocationRequest.create()
            .setInterval(10_000)
            .setFastestInterval(5_000)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setSmallestDisplacement(170f)

        client.requestLocationUpdates(request, callback, Looper.getMainLooper())

        awaitClose {
            client.removeLocationUpdates(callback)
        }
    }
}