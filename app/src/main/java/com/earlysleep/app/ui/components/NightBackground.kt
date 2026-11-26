package com.earlysleep.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.earlysleep.app.ui.theme.NightBackground
import com.earlysleep.app.ui.theme.NightBackgroundAlt
import kotlin.random.Random

@Composable
fun NightBackground(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.fillMaxSize()) {
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(NightBackground, NightBackgroundAlt, NightBackground)
            )
        )
        val random = Random(23)
        repeat(70) {
            val x = random.nextFloat() * size.width
            val y = random.nextFloat() * size.height * 0.8f
            val radius = 0.6.dp.toPx() + random.nextFloat() * 1.1.dp.toPx()
            drawCircle(
                color = Color.White.copy(alpha = 0.04f + random.nextFloat() * 0.06f),
                radius = radius,
                center = Offset(x, y)
            )
        }
    }
}
