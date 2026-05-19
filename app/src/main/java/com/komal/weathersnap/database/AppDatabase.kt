package com.komal.weathersnap.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [WeatherReportEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun reportDao(): WeatherReportDao
}