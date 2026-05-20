package com.komal.weathersnap.model

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class ReportDraft(
    val weather: WeatherReport,
    val imagePath: String? = null,
    val originalSize: Long = 0L,
    val compressedSize: Long = 0L,
    val notes: String = ""
)

@HiltViewModel
class SharedViewModel @Inject constructor(
    private val savedState: SavedStateHandle
) : ViewModel() {

    // WeatherReport fields persisted individually (SavedStateHandle needs Parcelable/primitives)
    private fun saveDraftToHandle(draft: ReportDraft?) {
        savedState["draft_city"]           = draft?.weather?.city
        savedState["draft_temp"]           = draft?.weather?.temperature
        savedState["draft_condition"]      = draft?.weather?.condition
        savedState["draft_humidity"]       = draft?.weather?.humidity
        savedState["draft_wind"]           = draft?.weather?.windSpeed
        savedState["draft_pressure"]       = draft?.weather?.pressure
        savedState["draft_imagePath"]      = draft?.imagePath
        savedState["draft_originalSize"]   = draft?.originalSize
        savedState["draft_compressedSize"] = draft?.compressedSize
        savedState["draft_notes"]          = draft?.notes
    }

    private fun loadDraftFromHandle(): ReportDraft? {
        val city = savedState.get<String>("draft_city") ?: return null
        return ReportDraft(
            weather = WeatherReport(
                city        = city,
                temperature = savedState.get<Double>("draft_temp")    ?: 0.0,
                condition   = savedState.get<String>("draft_condition") ?: "",
                humidity    = savedState.get<Int>("draft_humidity")   ?: 0,
                windSpeed   = savedState.get<Double>("draft_wind")    ?: 0.0,
                pressure    = savedState.get<Double>("draft_pressure") ?: 0.0
            ),
            imagePath      = savedState.get<String>("draft_imagePath"),
            originalSize   = savedState.get<Long>("draft_originalSize")   ?: 0L,
            compressedSize = savedState.get<Long>("draft_compressedSize") ?: 0L,
            notes          = savedState.get<String>("draft_notes")        ?: ""
        )
    }

    private val _draft = MutableStateFlow(loadDraftFromHandle())
    val draft: StateFlow<ReportDraft?> = _draft.asStateFlow()

    fun startDraft(weather: WeatherReport) {
        // Only reset if the weather changed — avoids overwriting photo/notes on rotation
        if (_draft.value?.weather != weather) {
            val new = ReportDraft(weather = weather)
            _draft.value = new
            saveDraftToHandle(new)
        }
    }

    fun updatePhoto(path: String, originalSize: Long, compressedSize: Long) {
        val updated = _draft.value?.copy(
            imagePath = path,
            originalSize = originalSize,
            compressedSize = compressedSize
        ) ?: return
        _draft.value = updated
        saveDraftToHandle(updated)
    }

    fun updateNotes(notes: String) {
        val updated = _draft.value?.copy(notes = notes) ?: return
        _draft.value = updated
        saveDraftToHandle(updated)
    }

    fun clearDraft() {
        _draft.value = null
        saveDraftToHandle(null)
    }
}