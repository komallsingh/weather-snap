package com.komal.weathersnap.model

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.komal.weathersnap.database.WeatherReportEntity
import com.komal.weathersnap.database.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

sealed class SaveState {
    object Idle    : SaveState()
    object Saving  : SaveState()
    object Success : SaveState()
    data class Error(val msg: String) : SaveState()
}

@HiltViewModel
class ReportViewModel @Inject constructor(
    private val repo: WeatherRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    val reports = repo.getReports()

    private val _saveState = MutableStateFlow<SaveState>(SaveState.Idle)
    val saveState: StateFlow<SaveState> = _saveState.asStateFlow()

    fun saveReport(draft: ReportDraft) {
        viewModelScope.launch {
            _saveState.value = SaveState.Saving
            runCatching {
                repo.saveReport(
                    WeatherReportEntity(
                        city           = draft.weather.city,
                        temperature    = draft.weather.temperature,
                        condition      = draft.weather.condition,
                        humidity       = draft.weather.humidity,
                        wind           = draft.weather.windSpeed,
                        pressure       = draft.weather.pressure,
                        notes          = draft.notes,
                        imagePath      = draft.imagePath ?: "",
                        originalSize   = draft.originalSize,
                        compressedSize = draft.compressedSize,
                        timestamp      = System.currentTimeMillis()
                    )
                )
            }
                .onSuccess { _saveState.value = SaveState.Success }
                .onFailure { _saveState.value = SaveState.Error(it.message ?: "Save failed") }
        }
    }

    fun resetSaveState() { _saveState.value = SaveState.Idle }
}