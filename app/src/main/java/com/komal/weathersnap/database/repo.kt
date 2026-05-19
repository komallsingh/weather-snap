package com.komal.weathersnap.database

import com.komal.weathersnap.data.GeocodingApi
import com.komal.weathersnap.data.WeatherApi
import com.komal.weathersnap.model.City
import com.komal.weathersnap.model.WeatherInfo
import com.komal.weathersnap.model.WeatherReport
import com.komal.weathersnap.model.weatherCondition
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val message: String) : Result<Nothing>()
}

@Singleton
class WeatherRepository @Inject constructor(
    private val geocodingApi: GeocodingApi,
    private val weatherApi: WeatherApi,
    private val dao: WeatherReportDao
) {
    // Simple in-memory cache: query -> list of cities
    private val cache = mutableMapOf<String, List<City>>()

    suspend fun searchCities(query: String): Result<List<City>> {
        cache[query.lowercase()]?.let { return Result.Success(it) }
        return try {
            val response = geocodingApi.searchCities(query)
            val cities = response.results?.map {
                City(it.id, it.name, it.latitude, it.longitude, it.country, it.admin1)
            } ?: emptyList()
            cache[query.lowercase()] = cities
            Result.Success(cities)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Search failed")
        }
    }

    suspend fun getWeather(city: City): Result<WeatherInfo> {
        return try {
            val response = weatherApi.getWeather(city.latitude, city.longitude)
            val current = response.current ?: return Result.Error("No data")
            val (condition, emoji) = weatherCondition(current.weatherCode ?: 0)
            Result.Success(
                WeatherInfo(
                    cityName = city.displayName,
                    latitude = city.latitude,
                    longitude = city.longitude,
                    temperature = current.temperature ?: 0.0,
                    condition = condition,
                    conditionEmoji = emoji,
                    humidity = current.humidity ?: 0,
                    windSpeed = current.windSpeed ?: 0.0,
                    pressure = current.pressure ?: 0.0
                )
            )
        } catch (e: Exception) {
            Result.Error(e.message ?: "Weather fetch failed")
        }
    }

    suspend fun saveReport(
        weather: WeatherInfo,
        imagePath: String?,
        originalSize: Long,
        compressedSize: Long,
        notes: String
    ): Long {
        return dao.insert(
            WeatherReportEntity(
                cityName = weather.cityName,
                temperature = weather.temperature,
                condition = weather.condition,
                humidity = weather.humidity,
                windSpeed = weather.windSpeed,
                pressure = weather.pressure,
                latitude = weather.latitude,
                longitude = weather.longitude,
                imagePath = imagePath,
                originalImageSize = originalSize,
                compressedImageSize = compressedSize,
                notes = notes
            )
        )
    }

    fun getAllReports(): Flow<List<WeatherReport>> {
        return dao.getAllReports().map { list ->
            list.map {
                WeatherReport(
                    id = it.id,
                    cityName = it.cityName,
                    temperature = it.temperature,
                    condition = it.condition,
                    humidity = it.humidity,
                    windSpeed = it.windSpeed,
                    pressure = it.pressure,
                    imagePath = it.imagePath,
                    originalImageSize = it.originalImageSize,
                    compressedImageSize = it.compressedImageSize,
                    notes = it.notes,
                    savedAt = it.savedAt
                )
            }
        }
    }
}