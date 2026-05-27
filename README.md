# WeatherSnap 🌤️

![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)
![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)
![Material 3](https://img.shields.io/badge/Material%203-6750A4?style=for-the-badge&logo=materialdesign&logoColor=white)
![MVVM](https://img.shields.io/badge/Architecture-MVVM-orange?style=for-the-badge)
![Coroutines](https://img.shields.io/badge/Coroutines-0095D5?style=for-the-badge&logo=kotlin&logoColor=white)
![Retrofit](https://img.shields.io/badge/Retrofit-2.11.0-48B983?style=for-the-badge)
![OkHttp](https://img.shields.io/badge/OkHttp-Logging%20Interceptor-black?style=for-the-badge)
![Room](https://img.shields.io/badge/Room-2.6.1-3DDC84?style=for-the-badge)
![CameraX](https://img.shields.io/badge/CameraX-1.3.4-red?style=for-the-badge)
![Coil](https://img.shields.io/badge/Coil-Image%20Loading-ff6f61?style=for-the-badge)
![Open-Meteo](https://img.shields.io/badge/API-OpenMeteo-blue?style=for-the-badge)

A production-grade Android weather app built with Jetpack Compose, CameraX, and Open-Meteo API. Search any city, capture a photo with a custom camera, add notes, and save weather reports — all stored locally with Room.

---

## Screenshots

| Weather Search | Create Report | Custom Camera | Saved Reports |
|:-:|:-:|:-:|:-:|
| Search cities with live autocomplete | Attach photo + notes to weather | Full CameraX custom camera UI | All saved reports with image + details |

---

## Features

- 🔍 **City autocomplete** — live suggestions after 2 characters, debounced to avoid excess API calls
- 💾 **Suggestion caching** — in-memory cache prevents duplicate network requests for the same query
- 🌡️ **Weather data** — temperature, condition, humidity, wind speed, pressure via Open-Meteo (no API key needed)
- 📷 **Custom CameraX screen** — full live preview, no system camera intent used
- 🗜️ **Image compression** — original vs compressed size displayed on both report creation and saved reports screens
- 📝 **Notes** — free-text notes attached to each report
- 🗃️ **Room persistence** — all reports saved locally with full weather snapshot, image path, sizes, and timestamp
- 🔄 **Draft recovery** — in-progress report survives rotation and process death (see Developer Challenge section)
- 🌙 **Dark theme** — full Material 3 dark UI throughout

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose + Material 3 |
| Architecture | MVVM |
| State | StateFlow + ViewModel |
| Async | Coroutines |
| Navigation | Navigation Compose |
| Network | Retrofit 2 + Gson + OkHttp logging interceptor |
| Local DB | Room |
| Camera | CameraX (camera-camera2, camera-lifecycle, camera-view) |
| Image loading | Coil |
| Permissions | Accompanist Permissions |

---

## API

Uses [Open-Meteo](https://open-meteo.com/) — **no API key required**.

| Purpose | Endpoint |
|---|---|
| City search / geocoding | `https://geocoding-api.open-meteo.com/v1/search` |
| Current weather | `https://api.open-meteo.com/v1/forecast` |

Weather fields fetched: `temperature_2m`, `relative_humidity_2m`, `wind_speed_10m`, `surface_pressure`, `weather_code`

---

## Project Structure

```
com.komal.weathersnap
├── apiIntegration/
│   └── Module.kt               # NetworkModule — two Retrofit instances (weather + geo)
├── data/
│   ├── GeocodingApi.kt         # Retrofit interface for city search
│   ├── GeoCodingResponse.kt    # Response models
│   ├── WeatherApi.kt           # Retrofit interface for forecast
│   └── WeatherResponse.kt      # Response models + weatherCode → condition mapping
├── database/
│   ├── AppDatabase.kt          # Room database
│   ├── Dao.kt                  # WeatherDao — insert, getAllReports
│   ├── Entity.kt               # WeatherReportEntity
│   ├── DatabaseModule.kt       # DatabaseModule — provides Room DB + DAO
│   └── WeatherRepository.kt    # Single data source — API + Room + cache
├── model/
│   ├── Models.kt               # City, WeatherReport domain models
│   ├── WeatherViewModel.kt     # City search + weather fetch, debounce, cache
│   ├── SharedViewModel.kt      # Draft state — SavedStateHandle persistence
│   ├── ReportViewModel.kt      # Save report to Room, expose reports Flow
│   └── CameraViewModel.kt      # CameraX bind, capture, compress
├── navigation/
│   └── Nav.kt                  # NavHost — SharedViewModel scoped to graph
├── Screens/
│   ├── WeatherHomeScreen.kt    # Search + suggestions + weather card
│   ├── ReportScreen.kt         # Weather snapshot + photo preview + notes + save
│   ├── CameraScreen.kt         # Custom CameraX full-screen camera
│   └── AllReportsScreen.kt     # LazyColumn of saved reports, empty state
├── utils/
│   └── Utils.kt                # formatBytes helper
├── WeatherSnapApp.kt           # Application class
└── MainActivity.kt             # Entry point, edge-to-edge
```

---

## Setup & Run

### Prerequisites

- Android Studio Hedgehog or newer
- JDK 17
- Android device or emulator running API 24+
- Internet connection (for weather API calls)

### Steps

```bash
# 1. Clone the repository
git clone https://github.com/your-username/WeatherSnap.git

# 2. Open in Android Studio
#    File → Open → select the WeatherSnap folder

# 3. Let Gradle sync complete
#    (all dependencies download automatically — no API keys needed)

# 4. Run on device or emulator
#    Press the ▶ Run button or Shift+F10
```

> **Camera on emulator:** Use a physical device for best results. If testing on emulator, enable the virtual camera in AVD settings (Edit → Camera → Front/Back → Webcam or Emulated).

### Permissions

The app requests the following at runtime:

- `CAMERA` — required for the custom CameraX screen

`INTERNET` is declared in the manifest and granted automatically.

---

## App Flow

```
WeatherHomeScreen
    │
    ├─ Type city name → autocomplete suggestions appear
    ├─ Tap suggestion → weather card loads
    │
    └─ [Create Report] button
            │
            └─ ReportScreen (CreateReport)
                    │
                    ├─ Weather snapshot shown (locked to selection)
                    ├─ [Capture Photo] → CameraScreen
                    │       │
                    │       └─ Live preview → tap shutter → compress → return
                    │
                    ├─ Photo preview + original/compressed sizes shown
                    ├─ Notes input
                    └─ [Save Report] → AllReportsScreen
```

---

## Developer Challenge — Draft Lifecycle Protection

### Problem

If a user selects weather, opens the Create Report screen, captures a photo, enters notes, then **rotates the device or the app is backgrounded and killed**, the in-progress draft must be recoverable — without creating duplicate saved reports.

### Solution: `SavedStateHandle` in `SharedViewModel`

`SharedViewModel` uses `SavedStateHandle` to persist draft fields (weather snapshot, image path, sizes, notes) as individual primitive/string keys. This survives:

- **Configuration changes** (rotation, split-screen resize) — because `SavedStateHandle` survives ViewModel recreation
- **Process death** — because `SavedStateHandle` is backed by the saved instance state bundle, which Android preserves across process death for the foreground task

```kotlin
class SharedViewModel(
    private val savedState: SavedStateHandle
) : ViewModel() {

    private fun saveDraftToHandle(draft: ReportDraft?) {
        savedState["draft_city"]      = draft?.weather?.city
        savedState["draft_temp"]      = draft?.weather?.temperature
        // ... all fields saved individually
    }

    private fun loadDraftFromHandle(): ReportDraft? {
        val city = savedState.get<String>("draft_city") ?: return null
        return ReportDraft( /* reconstruct from handle */ )
    }

    // Draft loaded from handle on init — survives process death
    private val _draft = MutableStateFlow(loadDraftFromHandle())
}
```

**Draft reset logic:** `startDraft()` only resets the draft if the weather data has changed — so rotating the device while on the Create Report screen does **not** wipe the captured photo or entered notes.

**Weather snapshot immutability:** The weather data is copied into the draft at the moment "Create Report" is tapped and stored in `SavedStateHandle`. It is never re-fetched. Even if the user backgrounds the app and the weather data on the home screen changes, the saved report always contains the exact snapshot from when report creation started.

**Temporary file cleanup:** Raw camera files are written to `cacheDir` and deleted immediately after compression. Compressed files go to `filesDir`. On `clearDraft()` (called after successful save), the draft is wiped. If the user discards without saving, the compressed file in `filesDir` remains until the OS clears it or the app is uninstalled — a known tradeoff documented here.

### Tradeoffs

| Approach | Chosen? | Reason |
|---|---|---|
| `SavedStateHandle` | ✅ Yes | Survives both rotation and process death; no extra DB table needed |
| Draft Room table | No | More robust for multi-draft scenarios, but adds complexity for a single-draft flow |
| `rememberSaveable` in Compose | No | Only survives rotation, not process death |
| DataStore | No | Async, adds latency; `SavedStateHandle` is synchronous and sufficient |

---

## Key Architecture Decisions

**Single `SharedViewModel` instance scoped to the NavGraph** — `SharedViewModel` is created once in `AppNavHost` and passed explicitly to all screens. This guarantees the camera screen and report screen share the same draft state across the entire navigation back stack.

**Two Retrofit instances** — The geocoding API (`geocoding-api.open-meteo.com`) and forecast API (`api.open-meteo.com`) have different base URLs and are provided as separate `@Named` Retrofit instances, both using the same `OkHttpClient`.

**Camera bind guard** — `AndroidView`'s `update` lambda fires on every recomposition. A boolean `isBound` flag in `CameraViewModel` prevents redundant `bindToLifecycle` calls that would otherwise throw `IllegalArgumentException`.

---

## Dependencies

```toml
# Core versions
agp        = "8.8.0"
kotlin     = "2.0.21"
ksp        = "2.0.21-1.0.27"
composeBom = "2024.09.00"
room       = "2.6.1"
retrofit   = "2.11.0"
cameraX    = "1.3.4"
```

Full dependency list is in `gradle/libs.versions.toml` and `app/build.gradle.kts`.

---

## Known Limitations

- Compressed image files in `filesDir` are not cleaned up if the user discards a report without saving (no explicit discard action exists)
- Weather condition is derived from WMO weather code — covers common codes; edge cases default to "Unknown"
- No offline mode — weather data requires an active internet connection

---

## License

```
MIT License — free to use, modify, and distribute.
```
