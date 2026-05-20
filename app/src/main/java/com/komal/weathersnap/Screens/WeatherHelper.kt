package com.komal.weathersnap.Screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Air
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.komal.weathersnap.model.WeatherReport

@Composable
 fun IdleState(textSecondary: Color) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 64.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            Icons.Outlined.Search,
            contentDescription = null,
            tint = textSecondary.copy(alpha = 0.4f),
            modifier = Modifier.size(64.dp)
        )
        Text(
            "Search for a city to see weather",
            color = textSecondary,
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
 fun LoadingState(accentColor: Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 64.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator(color = accentColor, strokeWidth = 2.dp)
            Text("Fetching weather...", color = accentColor.copy(alpha = 0.7f), fontSize = 13.sp)
        }
    }
}

@Composable
 fun ErrorState(message: String, textSecondary: Color) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            Icons.Outlined.ErrorOutline,
            contentDescription = null,
            tint = Color(0xFFE57373),
            modifier = Modifier.size(48.dp)
        )
        Text("Something went wrong", color = Color(0xFFE57373), fontSize = 15.sp, fontWeight = FontWeight.Medium)
        Text(message, color = textSecondary, fontSize = 12.sp, textAlign = TextAlign.Center)
    }
}

@Composable
 fun WeatherCard(
    report: WeatherReport,
    cardColor: Color,
    surfaceColor: Color,
    accentColor: Color,
    textPrimary: Color,
    textSecondary: Color,
    dividerColor: Color,
    onCreateReport: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Card(
            colors = CardDefaults.cardColors(containerColor = cardColor),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // City name + condition
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
                            Icon(
                                Icons.Outlined.LocationOn,
                                null,
                                tint = accentColor,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                report.city,
                                color = textPrimary,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 18.sp
                            )
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(report.condition, color = textSecondary, fontSize = 13.sp)
                    }
                    Text(
                        "${report.temperature.toInt()}°C",
                        color = textPrimary,
                        fontSize = 42.sp,
                        fontWeight = FontWeight.Light
                    )
                }

                HorizontalDivider(color = dividerColor, thickness = 0.5.dp)

                // Weather metrics grid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    WeatherMetric(
                        icon = Icons.Outlined.WaterDrop,
                        label = "Humidity",
                        value = "${report.humidity}%",
                        accentColor = accentColor,
                        textPrimary = textPrimary,
                        textSecondary = textSecondary
                    )
                    WeatherMetric(
                        icon = Icons.Outlined.Air,
                        label = "Wind",
                        value = "${report.windSpeed} km/h",
                        accentColor = accentColor,
                        textPrimary = textPrimary,
                        textSecondary = textSecondary
                    )
                    WeatherMetric(
                        icon = Icons.Outlined.Speed,
                        label = "Pressure",
                        value = "${report.pressure.toInt()} hPa",
                        accentColor = accentColor,
                        textPrimary = textPrimary,
                        textSecondary = textSecondary
                    )
                }
            }
        }

        // Create Report button
        Button(
            onClick = onCreateReport,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = accentColor)
        ) {
            Icon(Icons.Outlined.Add, null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text("Create Report", fontWeight = FontWeight.Medium, fontSize = 15.sp)
        }
    }
}

@Composable
 fun WeatherMetric(
    icon: ImageVector,
    label: String,
    value: String,
    accentColor: Color,
    textPrimary: Color,
    textSecondary: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(icon, null, tint = accentColor, modifier = Modifier.size(20.dp))
        Text(value, color = textPrimary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
        Text(label, color = textSecondary, fontSize = 11.sp)
    }
}