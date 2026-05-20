package com.komal.weathersnap.model


import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

data class CaptureResult(
    val path: String,
    val originalSize: Long,
    val compressedSize: Long
)

sealed class CameraState {
    object Idle      : CameraState()
    object Capturing : CameraState()
    data class Captured(val result: CaptureResult) : CameraState()
    data class Error(val msg: String)               : CameraState()
}

@HiltViewModel
class CameraViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _state = MutableStateFlow<CameraState>(CameraState.Idle)
    val state: StateFlow<CameraState> = _state.asStateFlow()

    private var imageCapture: ImageCapture? = null

    fun bindCamera(
        lifecycleOwner: LifecycleOwner,
        surfaceProvider: Preview.SurfaceProvider
    ) {
        val providerFuture = ProcessCameraProvider.getInstance(context)
        providerFuture.addListener({
            val cameraProvider = providerFuture.get()
            val preview = Preview.Builder().build().apply {
                setSurfaceProvider(surfaceProvider)
            }
            imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build()

            runCatching {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    imageCapture
                )
            }.onFailure { _state.value = CameraState.Error(it.message ?: "Camera bind failed") }
        }, ContextCompat.getMainExecutor(context))
    }

    fun capturePhoto() {
        val ic = imageCapture ?: return
        _state.value = CameraState.Capturing

        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val rawFile = File(context.cacheDir, "SNAP_${timestamp}_raw.jpg")

        val outputOptions = ImageCapture.OutputFileOptions.Builder(rawFile).build()

        ic.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    viewModelScope.launch {
                        runCatching { compress(rawFile) }
                            .onSuccess { _state.value = CameraState.Captured(it) }
                            .onFailure { _state.value = CameraState.Error(it.message ?: "Compress failed") }
                    }
                }
                override fun onError(exc: ImageCaptureException) {
                    _state.value = CameraState.Error(exc.message ?: "Capture failed")
                }
            }
        )
    }

    private fun compress(rawFile: File): CaptureResult {
        val originalSize = rawFile.length()
        val bitmap = BitmapFactory.decodeFile(rawFile.absolutePath)

        // Scale down to max 1080px on longest side
        val maxDim = 1080
        val scaled = if (bitmap.width > maxDim || bitmap.height > maxDim) {
            val scale = maxDim.toFloat() / maxOf(bitmap.width, bitmap.height)
            Bitmap.createScaledBitmap(
                bitmap,
                (bitmap.width * scale).toInt(),
                (bitmap.height * scale).toInt(),
                true
            )
        } else bitmap

        val compressedFile = File(
            context.filesDir,
            rawFile.name.replace("_raw.jpg", "_compressed.jpg")
        )
        FileOutputStream(compressedFile).use { out ->
            scaled.compress(Bitmap.CompressFormat.JPEG, 80, out)
        }

        rawFile.delete() // clean up temp raw file

        return CaptureResult(
            path           = compressedFile.absolutePath,
            originalSize   = originalSize,
            compressedSize = compressedFile.length()
        )
    }

    fun resetState() { _state.value = CameraState.Idle }
}