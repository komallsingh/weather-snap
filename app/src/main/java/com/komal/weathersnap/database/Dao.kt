package com.komal.weathersnap.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherReportDao {

    @Insert
    suspend fun insert(report: WeatherReportEntity): Long

    @Query("SELECT * FROM weather_reports ORDER BY savedAt DESC")
    fun getAllReports(): Flow<List<WeatherReportEntity>>
}