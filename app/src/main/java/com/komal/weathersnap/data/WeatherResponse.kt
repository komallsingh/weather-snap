package com.komal.weathersnap.data

import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    @SerializedName("current") val current: CurrentWeather
)

data class CurrentWeather(
    @SerializedName("temperature_2m")       val temperature: Double,
    @SerializedName("relative_humidity_2m") val humidity: Int,
    @SerializedName("wind_speed_10m")       val windSpeed: Double,
    @SerializedName("surface_pressure")     val pressure: Double,
    @SerializedName("weather_code")         val weatherCode: Int
) {
    val condition: String get() = weatherCodeToCondition(weatherCode)
}

private fun weatherCodeToCondition(code: Int): String = when (code) {
    0            -> "Clear sky"
    1            -> "Mainly clear"
    2            -> "Partly cloudy"
    3            -> "Overcast"
    in 45..48    -> "Foggy"
    in 51..55    -> "Drizzle"
    in 61..65    -> "Rain"
    in 71..75    -> "Snow"
    in 80..82    -> "Rain showers"
    in 95..99    -> "Thunderstorm"
    else         -> "Unknown"
}