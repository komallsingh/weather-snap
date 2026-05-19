package com.komal.weathersnap.database

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity
data class WeatherReportEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val city: String,
    val temperature: Double,
    val humidity: Int,
    val wind: Double,
    val pressure: Double,
    val notes: String,
    val imagePath: String,
    val originalSize: Long,
    val compressedSize: Long,
    val timestamp: Long
)