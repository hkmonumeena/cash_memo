package com.ruchitech.cashentery.ui.screens.common_ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ruchitech.cashentery.ui.theme.TempColor2
import com.ruchitech.cashentery.ui.theme.nonScaledSp

@Composable
fun VerticalMoneyRangeSlider(
    range: ClosedFloatingPointRange<Float>,
    onRangeChange: (ClosedFloatingPointRange<Float>) -> Unit,
) {
    val minValue = 0f
    val maxValue = 100000f
    val currentRange = remember { mutableStateOf(range) }

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
            .padding(bottom = 30.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "₹${currentRange.value.start.toInt()}  -  ₹${currentRange.value.endInclusive.toInt()}",
            fontSize = 18.sp.nonScaledSp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.weight(0.2F).padding(top = 16.dp)
        )
        RangeSlider(
            valueRange = minValue..maxValue,
            value = currentRange.value,
            onValueChange = { newRange ->
                currentRange.value = newRange
                onRangeChange(newRange)
            },
            steps = 1000,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1F)
                .clip(RoundedCornerShape(8.dp))
                .rotate(-90F),
            colors = SliderDefaults.colors(
                thumbColor = TempColor2,
                activeTickColor = TempColor2,
                activeTrackColor = TempColor2,
                inactiveTrackColor = Color.Gray,
                inactiveTickColor = Color.Gray
            )
        )
    }
}