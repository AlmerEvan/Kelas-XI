package com.example.weatherapp.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.weatherapp.NetworkResponse
import com.example.weatherapp.WeatherViewModel

@Composable
fun WeatherPage(viewModel: WeatherViewModel = viewModel()) {
    var city by remember { mutableStateOf("") }
    val weatherState by viewModel.weatherResult.observeAsState(NetworkResponse.Loading)
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Search bar
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = city,
                onValueChange = { city = it },
                label = { Text("Masukkan Kota") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp)
            )
            IconButton(onClick = {
                if (city.isNotEmpty()) {
                    viewModel.getData(city)
                    keyboardController?.hide()
                }
            }) {
                Icon(Icons.Default.Search, contentDescription = "Search")
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // State handling
        when (weatherState) {
            is NetworkResponse.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is NetworkResponse.Error<*> -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = (weatherState as NetworkResponse.Error<*>).message,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
            is NetworkResponse.Success<*> -> {
                @Suppress("UNCHECKED_CAST")
                val successState = weatherState as NetworkResponse.Success<com.example.weatherapp.model.WeatherModel>
                WeatherDetails(successState.data)
            }
        }
    }
}

