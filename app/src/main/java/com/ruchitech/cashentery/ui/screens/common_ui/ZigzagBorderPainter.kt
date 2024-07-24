package com.ruchitech.cashentery.ui.screens.common_ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.PaintingStyle.Companion.Fill
import androidx.compose.ui.graphics.PaintingStyle.Companion.Stroke
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import com.ruchitech.cashentery.ui.theme.MainBackgroundSurface
import kotlin.random.Random

fun DrawScope.drawZigzagBorder(
    color: Color,
    zigzagHeight: Float = 25f,
    zigzagWidth: Float = 22f
) {
    val random = Random.Default
    val path = Path().apply {
        var x = 0f
        var y = 0f
        moveTo(x, y)

        // Top edge zigzag
        while (x <= size.width) {
            val randomHeight = zigzagHeight * (0.5f + random.nextFloat())
            val randomWidth = zigzagWidth * (0.5f + random.nextFloat())
            lineTo(x + randomWidth / 2, y + randomHeight)
            lineTo(x + randomWidth, y)
            x += randomWidth
        }
        lineTo(size.width, 0f)
        // Right edge (straight line)
        lineTo(size.width, size.height)
        // Bottom edge zigzag
        x = size.width
        y = size.height
        while (x >= 0) {
            val randomHeight = zigzagHeight * (0.5f + random.nextFloat())
            val randomWidth = zigzagWidth * (0.5f + random.nextFloat())
            lineTo(x - randomWidth / 2, y - randomHeight)
            lineTo(x - randomWidth, y)
            x -= randomWidth
        }
        lineTo(0f, size.height)

        // Left edge (straight line)
        lineTo(0f, 0f)
    }

    val paint = Paint().apply {
        this.color = color
        this.style = PaintingStyle.Fill
    }

    drawIntoCanvas {
        it.drawPath(path, paint)
    }
}
@Composable
fun ZigzagBorderBox(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .background(Color.Transparent)
            .drawBehind {
                drawZigzagBorder(color = Color.White)
            }
            .padding(16.dp)
    ) {
        content()
    }
}
