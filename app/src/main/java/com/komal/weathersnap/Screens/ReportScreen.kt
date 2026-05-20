package com.komal.weathersnap.Screens

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.komal.weathersnap.model.*
import com.komal.weathersnap.navigation.Screen
import java.io.File
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(
    navController: NavController,
    sharedVm: SharedViewModel = hiltViewModel(),
    reportVm: ReportViewModel = hiltViewModel()
) {
    val draft by sharedVm.draft.collectAsState()
    val saveState by reportVm.saveState.collectAsState()

    val bgColor = Color(0xFF0F1117)
    val surfaceColor = Color(0xFF1A1D27)
    val cardColor = Color(0xFF1E2130)
    val accentColor = Color(0xFF4F8EF7)
    val textPrimary = Color(0xFFE8EAF0)
    val textSecondary = Color(0xFF8B90A4)
    val dividerColor = Color(0xFF2A2D3E)

    // Navigate to all reports after successful save
    LaunchedEffect(saveState) {
        if (saveState is SaveState.Success) {
            sharedVm.clearDraft()
            reportVm.resetSaveState()
            navController.navigate(Screen.AllReports.route) {
                popUpTo(Screen.WeatherHome.route)
            }
        }
    }

    Scaffold(
        containerColor = bgColor,
        topBar = {
            TopAppBar(
                title = {
                    Text("Create Report", color = textPrimary, fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Filled.ArrowBack, null, tint = textPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = bgColor)
            )
        }
    ) { padding ->
        if (draft == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No weather data selected.", color = textSecondary)
            }
            return@Scaffold
        }

        val weather = draft!!.weather

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(Modifier.height(4.dp))

            // Weather snapshot card
            Card(
                colors = CardDefaults.cardColors(containerColor = cardColor),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(6.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "Weather Snapshot",
                        color = textSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 1.sp
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(Icons.Outlined.LocationOn, null, tint = accentColor, modifier = Modifier.size(14.dp))
                                Text(weather.city, color = textPrimary, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                            }
                            Text(weather.condition, color = textSecondary, fontSize = 12.sp)
                        }
                        Text("${weather.temperature.toInt()}°C", color = textPrimary, fontSize = 32.sp, fontWeight = FontWeight.Light)
                    }
                    HorizontalDivider(color = dividerColor, thickness = 0.5.dp)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        MiniMetric("💧", "${weather.humidity}%", "Humidity", textPrimary, textSecondary)
                        MiniMetric("💨", "${weather.windSpeed} km/h", "Wind", textPrimary, textSecondary)
                        MiniMetric("🔵", "${weather.pressure.toInt()} hPa", "Pressure", textPrimary, textSecondary)
                    }
                }
            }

            // Photo section
            Card(
                colors = CardDefaults.cardColors(containerColor = cardColor),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(6.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "Photo",
                        color = textSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 1.sp
                    )

                    AnimatedContent(
                        targetState = draft!!.imagePath,
                        transitionSpec = { fadeIn(tween(400)) togetherWith fadeOut(tween(200)) },
                        label = "photo_preview"
                    ) { imagePath ->
                        if (imagePath != null) {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(File(imagePath))
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = "Captured photo",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp)
                                        .clip(RoundedCornerShape(12.dp)),
                                    contentScale = ContentScale.Crop
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    SizeChip(
                                        label = "Original",
                                        bytes = draft!!.originalSize,
                                        color = Color(0xFF8B90A4),
                                        textSecondary = textSecondary
                                    )
                                    SizeChip(
                                        label = "Compressed",
                                        bytes = draft!!.compressedSize,
                                        color = Color(0xFF4CAF50),
                                        textSecondary = textSecondary
                                    )
                                }
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(140.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(surfaceColor)
                                    .border(1.dp, dividerColor, RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(Icons.Outlined.Camera, null, tint = textSecondary.copy(alpha = 0.5f), modifier = Modifier.size(36.dp))
                                    Text("No photo captured yet", color = textSecondary, fontSize = 13.sp)
                                }
                            }
                        }
                    }

                    OutlinedButton(
                        onClick = { navController.navigate(Screen.Camera.route) },
                        modifier = Modifier.fillMaxWidth().height(44.dp),
                        shape = RoundedCornerShape(10.dp),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = androidx.compose.ui.graphics.SolidColor(accentColor)
                        )
                    ) {
                        Icon(Icons.Outlined.Cameraswitch, null, tint = accentColor, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            if (draft!!.imagePath != null) "Retake Photo" else "Capture Photo",
                            color = accentColor,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            // Notes section
            Card(
                colors = CardDefaults.cardColors(containerColor = cardColor),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(6.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "Notes",
                        color = textSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 1.sp
                    )
                    OutlinedTextField(
                        value = draft!!.notes,
                        onValueChange = sharedVm::updateNotes,
                        modifier = Modifier.fillMaxWidth().height(120.dp),
                        placeholder = { Text("Add notes about this weather report...", color = textSecondary, fontSize = 13.sp) },
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = accentColor,
                            unfocusedBorderColor = dividerColor,
                            focusedContainerColor = surfaceColor,
                            unfocusedContainerColor = surfaceColor,
                            cursorColor = accentColor,
                            focusedTextColor = textPrimary,
                            unfocusedTextColor = textPrimary
                        ),
                        maxLines = 5
                    )
                }
            }

            // Error state
            AnimatedVisibility(visible = saveState is SaveState.Error) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFE57373).copy(alpha = 0.12f))
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Outlined.Warning, null, tint = Color(0xFFE57373), modifier = Modifier.size(16.dp))
                    Text((saveState as? SaveState.Error)?.msg ?: "", color = Color(0xFFE57373), fontSize = 13.sp)
                }
            }

            // Save button
            Button(
                onClick = { reportVm.saveReport(draft!!) },
                enabled = saveState !is SaveState.Saving,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = accentColor)
            ) {
                if (saveState is SaveState.Saving) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(Icons.Outlined.SaveAlt, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Save Report", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}