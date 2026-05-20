package com.komal.weathersnap.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.komal.weathersnap.database.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class WeatherUiState {
    object Idle    : WeatherUiState()
    object Loading : WeatherUiState()
    data class Success(val report: WeatherReport) : WeatherUiState()
    data class Error(val message: String)         : WeatherUiState()
}

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val repo: WeatherRepository
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _suggestions = MutableStateFlow<List<City>>(emptyList())
    val suggestions: StateFlow<List<City>> = _suggestions.asStateFlow()

    private val _uiState = MutableStateFlow<WeatherUiState>(WeatherUiState.Idle)
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    private val _selectedCity = MutableStateFlow<City?>(null)
    val selectedCity: StateFlow<City?> = _selectedCity.asStateFlow()

    init {
        @OptIn(FlowPreview::class)
        viewModelScope.launch {
            _query
                .debounce(300)
                .filter { it.length > 2 }
                .distinctUntilChanged()
                .collect { q ->
                    runCatching { repo.searchCity(q) }
                        .onSuccess { _suggestions.value = it }
                        .onFailure { _suggestions.value = emptyList() }
                }
        }
    }

    fun onQueryChange(q: String) {
        _query.value = q
        if (q.length <= 2) _suggestions.value = emptyList()
    }

    fun selectCity(city: City) {
        _selectedCity.value = city
        _query.value = city.name
        _suggestions.value = emptyList()
        fetchWeather(city)
    }

    private fun fetchWeather(city: City) {
        viewModelScope.launch {
            _uiState.value = WeatherUiState.Loading
            runCatching { repo.getWeather(city) }
                .onSuccess { resp ->
                    _uiState.value = WeatherUiState.Success(
                        WeatherReport(
                            city        = city.displayName,
                            temperature = resp.current.temperature,
                            condition   = resp.current.condition,
                            humidity    = resp.current.humidity,
                            windSpeed   = resp.current.windSpeed,
                            pressure    = resp.current.pressure
                        )
                    )
                }
                .onFailure { _uiState.value = WeatherUiState.Error(it.message ?: "Unknown error") }
        }
    }
}