package com.komal.weathersnap.model


data class City(
    val id: Long,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val country: String?,
    val admin1: String?
) {
    val displayName: String get() = buildString {
        append(name)
        if (!admin1.isNullOrBlank()) append(", $admin1")
        if (!country.isNullOrBlank()) append(", $country")
    }
}

data class WeatherInfo(
    val cityName: String,
    val latitude: Double,
    val longitude: Double,
    val temperature: Double,
    val condition: String,
    val conditionEmoji: String,
    val humidity: Int,
    val windSpeed: Double,
    val pressure: Double
)

data class WeatherReport(
    val id: Long,
    val cityName: String,
    val temperature: Double,
    val condition: String,
    val humidity: Int,
    val windSpeed: Double,
    val pressure: Double,
    val imagePath: String?,
    val originalImageSize: Long,
    val compressedImageSize: Long,
    val notes: String,
    val savedAt: Long
)

fun weatherCondition(code: Int): Pair<String, String> = when (code) {
    0          -> "Clear Sky"    to "☀️"
    1          -> "Mainly Clear" to "🌤️"
    2          -> "Partly Cloudy" to "⛅"
    3          -> "Overcast"     to "☁️"
    45, 48     -> "Foggy"        to "🌫️"
    in 51..55  -> "Drizzle"      to "🌦️"
    in 61..65  -> "Rain"         to "🌧️"
    in 71..75  -> "Snow"         to "❄️"
    in 80..82  -> "Rain Showers" to "🌦️"
    95         -> "Thunderstorm" to "⛈️"
    else       -> "Unknown"      to "🌡️"
}