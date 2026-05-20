package com.komal.weathersnap.Screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.komal.weathersnap.model.*
import com.komal.weathersnap.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherHomeScreen(
    navController: NavController,
    weatherVm: WeatherViewModel = hiltViewModel(),
    sharedVm: SharedViewModel = hiltViewModel()
) {
    val query by weatherVm.query.collectAsState()
    val suggestions by weatherVm.suggestions.collectAsState()
    val uiState by weatherVm.uiState.collectAsState()

    val bgColor = Color(0xFF0F1117)
    val surfaceColor = Color(0xFF1A1D27)
    val cardColor = Color(0xFF1E2130)
    val accentColor = Color(0xFF4F8EF7)
    val textPrimary = Color(0xFFE8EAF0)
    val textSecondary = Color(0xFF8B90A4)
    val dividerColor = Color(0xFF2A2D3E)

    Scaffold(
        containerColor = bgColor,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "WeatherSnap",
                        color = textPrimary,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 20.sp
                    )
                },
                actions = {
                    TextButton(
                        onClick = { navController.navigate(Screen.AllReports.route) }
                    ) {
                        Icon(
                            Icons.Outlined.List,
                            contentDescription = "Reports",
                            tint = accentColor,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text("Reports", color = accentColor, fontSize = 14.sp)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = bgColor)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Spacer(Modifier.height(8.dp))
                // Search field
                Column {
                    OutlinedTextField(
                        value = query,
                        onValueChange = weatherVm::onQueryChange,
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text("Search city...", color = textSecondary)
                        },
                        leadingIcon = {
                            Icon(Icons.Outlined.Search, null, tint = textSecondary)
                        },
                        trailingIcon = {
                            if (query.isNotEmpty()) {
                                IconButton(onClick = { weatherVm.onQueryChange("") }) {
                                    Icon(Icons.Filled.Close, null, tint = textSecondary)
                                }
                            }
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = accentColor,
                            unfocusedBorderColor = dividerColor,
                            focusedContainerColor = surfaceColor,
                            unfocusedContainerColor = surfaceColor,
                            cursorColor = accentColor,
                            focusedTextColor = textPrimary,
                            unfocusedTextColor = textPrimary
                        )
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "Enter more than 2 letters to start city suggestions.",
                        color = textSecondary,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }

            // Suggestions
            if (suggestions.isNotEmpty()) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = surfaceColor),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column {
                            suggestions.forEachIndexed { index, city ->
                                AnimatedVisibility(
                                    visible = true,
                                    enter = fadeIn() + slideInVertically(
                                        initialOffsetY = { it / 2 },
                                        animationSpec = tween(200 + index * 40)
                                    )
                                ) {
                                    Column {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable { weatherVm.selectCity(city) }
                                                .padding(horizontal = 16.dp, vertical = 12.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                                        ) {
                                            Icon(
                                                Icons.Outlined.LocationOn,
                                                contentDescription = null,
                                                tint = accentColor,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    city.name,
                                                    color = textPrimary,
                                                    fontSize = 14.sp,
                                                    fontWeight = FontWeight.Medium
                                                )
                                                if (!city.admin1.isNullOrBlank() || !city.country.isNullOrBlank()) {
                                                    Text(
                                                        listOfNotNull(city.admin1, city.country).joinToString(", "),
                                                        color = textSecondary,
                                                        fontSize = 12.sp
                                                    )
                                                }
                                            }
                                        }
                                        if (index < suggestions.lastIndex) {
                                            HorizontalDivider(color = dividerColor, thickness = 0.5.dp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Weather States
            item {
                AnimatedContent(
                    targetState = uiState,
                    transitionSpec = {
                        fadeIn(tween(300)) togetherWith fadeOut(tween(200))
                    },
                    label = "weather_state"
                ) { state ->
                    when (state) {
                        is WeatherUiState.Idle -> IdleState(textSecondary)
                        is WeatherUiState.Loading -> LoadingState(accentColor)
                        is WeatherUiState.Error -> ErrorState(state.message, textSecondary)
                        is WeatherUiState.Success -> WeatherCard(
                            report = state.report,
                            cardColor = cardColor,
                            surfaceColor = surfaceColor,
                            accentColor = accentColor,
                            textPrimary = textPrimary,
                            textSecondary = textSecondary,
                            dividerColor = dividerColor,
                            onCreateReport = {
                                sharedVm.startDraft(state.report)
                                navController.navigate(Screen.Report.route)
                            }
                        )
                    }
                }
            }

            item { Spacer(Modifier.height(24.dp)) }
        }
    }
}

