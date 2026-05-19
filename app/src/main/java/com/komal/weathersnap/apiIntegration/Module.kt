package com.komal.weathersnap.apiIntegration

import android.content.Context
import androidx.room.Room
import com.komal.weathersnap.data.GeocodingApi
import com.komal.weathersnap.data.WeatherApi
import com.komal.weathersnap.database.AppDatabase
import com.komal.weathersnap.database.WeatherReportDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // OkHttp Client (NO BuildConfig, gave unknown error)
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }

    //Geocoding Retrofit
    @Provides
    @Singleton
    @Named("geocoding")
    fun provideGeocodingRetrofit(
        client: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://geocoding-api.open-meteo.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    //Weather Retrofit
    @Provides
    @Singleton
    @Named("weather")
    fun provideWeatherRetrofit(
        client: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.open-meteo.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    //APIs
    @Provides
    @Singleton
    fun provideGeocodingApi(
        @Named("geocoding") retrofit: Retrofit
    ): GeocodingApi = retrofit.create(GeocodingApi::class.java)

    @Provides
    @Singleton
    fun provideWeatherApi(
        @Named("weather") retrofit: Retrofit
    ): WeatherApi = retrofit.create(WeatherApi::class.java)

    //Room Database
    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "weathersnap.db"
        ).build()
    }

    //DAO
    @Provides
    @Singleton
    fun provideWeatherReportDao(
        db: AppDatabase
    ): WeatherReportDao = db.reportDao()
}