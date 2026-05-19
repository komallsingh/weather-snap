package com.komal.weathersnap.database

import com.komal.weathersnap.data.GeocodingApi
import com.komal.weathersnap.data.WeatherApi
import com.komal.weathersnap.data.WeatherResponse
import com.komal.weathersnap.model.City
import com.komal.weathersnap.model.WeatherReport
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.flow.Flow

@Singleton // Added this annotation
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
                        id = it.id,
                        name = it.name,
                        latitude = it.latitude,
                        longitude = it.longitude,
                        country = it.country,
                        admin1 = it.admin1
                    )
                } ?: emptyList()

            cityCache[query] = result
            result
        }
    }
    suspend fun getWeather(city: City): WeatherResponse {
        return weatherApi.getWeather(city.latitude, city.longitude)
    }


    suspend fun saveReport(report: WeatherReportEntity) =
        dao.insert(report)

    fun getReports(): Flow<List<WeatherReportEntity>> = dao.getAllReports()
}
