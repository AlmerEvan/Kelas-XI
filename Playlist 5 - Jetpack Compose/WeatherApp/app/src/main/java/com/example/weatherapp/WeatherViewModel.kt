package com.example.weatherapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.model.WeatherModel
import com.example.weatherapp.network.RetrofitInstance
import kotlinx.coroutines.launch

class WeatherViewModel : ViewModel() {
    private val _weatherResult = MutableLiveData<NetworkResponse<WeatherModel>>()
    val weatherResult: LiveData<NetworkResponse<WeatherModel>> = _weatherResult

    fun getData(city: String) {
        _weatherResult.value = NetworkResponse.Loading
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getWeather(Constants.API_KEY, city)
                if (response.isSuccessful && response.body() != null) {
                    _weatherResult.value = NetworkResponse.Success(response.body()!!)
                } else {
                    _weatherResult.value = NetworkResponse.Error("Error: ${response.message()}")
                }
            } catch (e: Exception) {
                _weatherResult.value = NetworkResponse.Error("Exception: ${e.localizedMessage}")
            }
        }
    }
}
