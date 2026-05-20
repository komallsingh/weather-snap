package com.komal.weathersnap.model

data class City(
    val id: Int,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val country: String?,
    val admin1: String?
) {
    val displayName: String
        get() = buildString {
            append(name)
            if (!admin1.isNullOrBlank()) append(", $admin1")
            if (!country.isNullOrBlank()) append(", $country")
        }
}

data class WeatherReport(
    val city: String,
    val temperature: Double,
    val condition: String,
    val humidity: Int,
    val windSpeed: Double,
    val pressure: Double
)