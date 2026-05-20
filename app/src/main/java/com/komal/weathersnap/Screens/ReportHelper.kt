package com.komal.weathersnap.Screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MiniMetric(emoji: String, value: String, label: String, textPrimary: Color, textSecondary: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(emoji, fontSize = 14.sp)
        Text(value, color = textPrimary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
        Text(label, color = textSecondary, fontSize = 10.sp)
    }
}

@Composable
 fun SizeChip(label: String, bytes: Long, color: Color, textSecondary: Color) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color.copy(alpha = 0.1f))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = color, fontSize = 11.sp, fontWeight = FontWeight.Medium)
        Text(formatBytes(bytes), color = color, fontSize = 11.sp)
    }
}
fun formatBytes(bytes: Long): String {
    return when {
        bytes >= 1_048_576 -> "%.1f MB".format(bytes / 1_048_576.0)
        bytes >= 1024 -> "%.1f KB".format(bytes / 1024.0)
        else -> "$bytes B"
    }
}
