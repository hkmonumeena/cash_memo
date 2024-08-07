package com.ruchitech.cashentery.ui.screens.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ruchitech.cashentery.R
import com.ruchitech.cashentery.ui.theme.MainBackgroundSurface
import com.ruchitech.cashentery.ui.theme.nonScaledSp
import java.text.NumberFormat
import java.util.Locale

fun formatToINR(amount: Double): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
    return formatter.format(amount)
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SumCountCard(
    modifier: Modifier,
    level: String,
    iconBg: Color,
    iconTint: Color,
    rotationIcon: Float,
    amount: Double,
    onClick:()->Unit
) {
    Card(
        onClick = {
            onClick()
        },
        colors = CardDefaults.cardColors(containerColor = MainBackgroundSurface),
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp, horizontal = 5.dp),
        border = BorderStroke(0.5.dp, color = Color(0xFFBBA76D))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = level, fontSize = 14.sp.nonScaledSp, color = Color.Gray)
            Box(
                modifier = Modifier
                    .background(iconBg, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painterResource(id = R.drawable.ic_arrow),
                    contentDescription = null,
                    modifier = Modifier
                        .rotate(rotationIcon)
                        .padding(2.dp)
                        .size(25.dp),
                    tint = iconTint
                )
            }
        }

        Text(
            text = formatToINR(amount),
            fontSize = 18.sp.nonScaledSp,
            color = Color.Black,
            modifier = Modifier.padding(horizontal = 16.dp),
            fontWeight = FontWeight.ExtraBold
        )
        Spacer(modifier = Modifier.height(10.dp))

    }
}