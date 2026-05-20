package com.komal.weathersnap.database

import com.komal.weathersnap.data.GeocodingApi
import com.komal.weathersnap.data.WeatherApi
import com.komal.weathersnap.data.WeatherResponse
import com.komal.weathersnap.model.City
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject      // ← changed from jakarta to javax
import javax.inject.Singleton   // ← changed from jakarta to javax

@Singleton
class WeatherRepository @Inject constructor(
    private val geoApi: GeocodingApi,
    private val weatherApi: WeatherApi,
    private val dao: WeatherDao
) {
    private val cityCache = mutableMapOf<String, List<City>>()

    suspend fun searchCity(query: String): List<City> {
        return cityCache[query] ?: run {
            val result = geoApi.searchCities(query).results
                ?.map {
                    City(
                        id        = it.id.toInt(),   // ← removed .toInt(), id is already Int
                        name      = it.name,
                        latitude  = it.latitude,
                        longitude = it.longitude,
                        country   = it.country,
                        admin1    = it.admin1
                    )
                } ?: emptyList()
            cityCache[query] = result
            result
        }
    }

    suspend fun getWeather(city: City): WeatherResponse =
        weatherApi.getWeather(city.latitude, city.longitude)

    suspend fun saveReport(report: WeatherReportEntity) =
        dao.insert(report)

    fun getReports(): Flow<List<WeatherReportEntity>> =
        dao.getAllReports()
}