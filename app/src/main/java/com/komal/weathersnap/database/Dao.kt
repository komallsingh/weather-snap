package com.komal.weathersnap.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDao {

    @Insert
    suspend fun insert(report: WeatherReportEntity): Long
    @Query("SELECT * FROM WeatherReportEntity ORDER BY timestamp DESC")
    fun getAllReports(): Flow<List<WeatherReportEntity>>

    @Query("DELETE FROM reports WHERE id = :id")
    suspend fun deleteById(id: Int)
}