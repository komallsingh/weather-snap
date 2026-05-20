// com/komal/weathersnap/Screens/CameraScreen.kt
package com.komal.weathersnap.Screens

import android.Manifest
import androidx.camera.view.PreviewView
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.permissions.*
import com.komal.weathersnap.model.*

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraScreen(
    navController: NavController,
    cameraVm: CameraViewModel = hiltViewModel(),
    sharedVm: SharedViewModel
) {
    val cameraState by cameraVm.state.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current

    val bgColor = Color(0xFF000000)
    val accentColor = Color(0xFF4F8EF7)
    val textPrimary = Color(0xFFE8EAF0)
    val textSecondary = Color(0xFF8B90A4)

    val cameraPermission = rememberPermissionState(Manifest.permission.CAMERA)

    // Handle successful capture — update shared draft and go back
    LaunchedEffect(cameraState) {
        if (cameraState is CameraState.Captured) {
            val result = (cameraState as CameraState.Captured).result
            sharedVm.updatePhoto(result.path, result.originalSize, result.compressedSize)
            navController.navigateUp()
        }
    }

    LaunchedEffect(Unit) {
        if (!cameraPermission.status.isGranted) {
            cameraPermission.launchPermissionRequest()
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(bgColor)) {
        if (cameraPermission.status.isGranted) {
            // Camera preview — full screen
            AndroidView(
                factory = { ctx ->
                    PreviewView(ctx).apply {
                        implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                        scaleType = PreviewView.ScaleType.FILL_CENTER
                    }
                },
                modifier = Modifier.fillMaxSize(),
                update = { previewView ->
                    cameraVm.bindCamera(lifecycleOwner, previewView.surfaceProvider)
                }
            )
        } else {
            // Permission denied state
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text("Camera permission required", color = textPrimary, fontSize = 16.sp)
                    Button(
                        onClick = { cameraPermission.launchPermissionRequest() },
                        colors = ButtonDefaults.buttonColors(containerColor = accentColor)
                    ) {
                        Text("Grant Permission")
                    }
                }
            }
        }

        // Top bar overlay
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Black.copy(alpha = 0.55f))
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Custom Camera",
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp
                )
                IconButton(
                    onClick = { navController.navigateUp() },
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.15f))
                ) {
                    Icon(Icons.Filled.Close, null, tint = Color.White, modifier = Modifier.size(20.dp))
                }
            }
        }

        // Bottom controls overlay
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(Color.Black.copy(alpha = 0.55f))
                .navigationBarsPadding()
                .padding(bottom = 32.dp, top = 20.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Capture state feedback
                AnimatedVisibility(
                    visible = cameraState is CameraState.Capturing,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            color = accentColor,
                            modifier = Modifier.size(14.dp),
                            strokeWidth = 2.dp
                        )
                        Text("Capturing...", color = Color.White, fontSize = 13.sp)
                    }
                }

                AnimatedVisibility(
                    visible = cameraState is CameraState.Error,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Text(
                        (cameraState as? CameraState.Error)?.msg ?: "",
                        color = Color(0xFFE57373),
                        fontSize = 13.sp
                    )
                }

                // Capture button — large circular shutter
                Box(contentAlignment = Alignment.Center) {
                    // Outer ring
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .clip(CircleShape)
                            .border(2.5.dp, Color.White.copy(alpha = 0.8f), CircleShape)
                    )
                    // Inner shutter button
                    IconButton(
                        onClick = {
                            if (cameraState !is CameraState.Capturing) {
                                cameraVm.capturePhoto()
                            }
                        },
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(
                                if (cameraState is CameraState.Capturing)
                                    accentColor.copy(alpha = 0.7f)
                                else Color.White
                            )
                    ) {
                        if (cameraState is CameraState.Capturing) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(22.dp),
                                strokeWidth = 2.dp
                            )
                        }
                    }
                }

                Text(
                    "Tap to capture",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 12.sp
                )
            }
        }
    }
}