package com.komal.weathersnap.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather_reports")
data class WeatherReportEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val cityName: String,
    val temperature: Double,
    val condition: String,
    val humidity: Int,
    val windSpeed: Double,
    val pressure: Double,
    val latitude: Double,
    val longitude: Double,
    val imagePath: String?,
    val originalImageSize: Long,
    val compressedImageSize: Long,
    val notes: String,
    val savedAt: Long = System.currentTimeMillis()
)