// com/komal/weathersnap/Screens/AllReportsScreen.kt
package com.komal.weathersnap.Screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.komal.weathersnap.database.WeatherReportEntity
import com.komal.weathersnap.model.ReportViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllReportsScreen(
    navController: NavController,
    reportVm: ReportViewModel = hiltViewModel()
) {
    val reports by reportVm.reports.collectAsState(initial = emptyList())

    val bgColor = Color(0xFF0F1117)
    val cardColor = Color(0xFF1E2130)
    val surfaceColor = Color(0xFF1A1D27)
    val accentColor = Color(0xFF4F8EF7)
    val textPrimary = Color(0xFFE8EAF0)
    val textSecondary = Color(0xFF8B90A4)
    val dividerColor = Color(0xFF2A2D3E)

    Scaffold(
        containerColor = bgColor,
        topBar = {
            TopAppBar(
                title = {
                    Text("Saved Reports", color = textPrimary, fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
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
        if (reports.isEmpty()) {
            // Empty state
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        Icons.Outlined.List,
                        null,
                        tint = textSecondary.copy(alpha = 0.4f),
                        modifier = Modifier.size(72.dp)
                    )
                    Text(
                        "No reports yet",
                        color = textPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        "Create your first weather report\nfrom the home screen.",
                        color = textSecondary,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item { Spacer(Modifier.height(4.dp)) }

                items(reports, key = { it.id }) { report ->
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn() + slideInVertically(initialOffsetY = { it / 3 })
                    ) {
                        ReportCard(
                            report = report,
                            cardColor = cardColor,
                            surfaceColor = surfaceColor,
                            accentColor = accentColor,
                            textPrimary = textPrimary,
                            textSecondary = textSecondary,
                            dividerColor = dividerColor
                        )
                    }
                }

                item { Spacer(Modifier.height(16.dp)) }
            }
        }
    }
}

@Composable
private fun ReportCard(
    report: WeatherReportEntity,
    cardColor: Color,
    surfaceColor: Color,
    accentColor: Color,
    textPrimary: Color,
    textSecondary: Color,
    dividerColor: Color
) {
    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy · hh:mm a", Locale.getDefault()) }

    Card(
        colors = CardDefaults.cardColors(containerColor = cardColor),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {

            // Photo
            if (report.imagePath.isNotBlank() && File(report.imagePath).exists()) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(File(report.imagePath))
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // City + temp + timestamp
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Outlined.LocationOn, null, tint = accentColor, modifier = Modifier.size(14.dp))
                            Text(report.city, color = textPrimary, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                        }
                        Text(report.condition, color = textSecondary, fontSize = 12.sp)
                    }
                    Text("${report.temperature.toInt()}°C", color = textPrimary, fontSize = 28.sp, fontWeight = FontWeight.Light)
                }

                HorizontalDivider(color = dividerColor, thickness = 0.5.dp)

                // Weather metrics row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    ReportMetric("💧", "${report.humidity}%", "Humidity", textPrimary, textSecondary)
                    ReportMetric("💨", "${report.wind} km/h", "Wind", textPrimary, textSecondary)
                    ReportMetric("🔵", "${report.pressure.toInt()} hPa", "Pressure", textPrimary, textSecondary)
                }

                // Notes
                if (report.notes.isNotBlank()) {
                    HorizontalDivider(color = dividerColor, thickness = 0.5.dp)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(surfaceColor)
                            .padding(10.dp)
                    ) {
                        Icon(Icons.Outlined.Email, null, tint = textSecondary, modifier = Modifier.size(16.dp))
                        Text(report.notes, color = textSecondary, fontSize = 13.sp, lineHeight = 18.sp)
                    }
                }

                HorizontalDivider(color = dividerColor, thickness = 0.5.dp)

                // Image sizes + timestamp
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        SizeTag("Original", report.originalSize, Color(0xFF8B90A4))
                        SizeTag("Compressed", report.compressedSize, Color(0xFF4CAF50))
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(Icons.Outlined.Warning, null, tint = textSecondary, modifier = Modifier.size(12.dp))
                    Text(
                        dateFormat.format(Date(report.timestamp)),
                        color = textSecondary,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun ReportMetric(emoji: String, value: String, label: String, textPrimary: Color, textSecondary: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(emoji, fontSize = 14.sp)
        Text(value, color = textPrimary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
        Text(label, color = textSecondary, fontSize = 10.sp)
    }
}

@Composable
private fun SizeTag(label: String, bytes: Long, color: Color) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color.copy(alpha = 0.1f))
            .padding(horizontal = 6.dp, vertical = 3.dp),
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = color, fontSize = 10.sp, fontWeight = FontWeight.Medium)
        Text(formatBytes(bytes), color = color, fontSize = 10.sp)
    }
}
