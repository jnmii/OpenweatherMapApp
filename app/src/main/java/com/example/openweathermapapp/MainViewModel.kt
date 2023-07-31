package com.example.openweathermapapp


import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import com.example.openweathermapapp.service.WeatherRepository
import com.example.openweathermapapp.service.dto.CurrentWeather
import com.example.openweathermapapp.service.dto.FullWeather

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@SuppressLint("MissingPermission")
@HiltViewModel
class MainViewModel @Inject constructor(
    repository: WeatherRepository
) : ViewModel() {

    val current: Flow<CurrentWeather> = repository.getCurrentWeather()
    val forecast: Flow<List<FullWeather.Daily>> = repository.getFiveDayForecast()

}