package com.ruchitech.cashentery.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ruchitech.cashentery.R

// Set of Material typography styles to start with
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
    /* Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
)

val TextUnit.nonScaledSp
    @Composable
    get() = (this.value / androidx.compose.ui.platform.LocalDensity.current.fontScale).sp

val TextUnit.nonScaledDp
    @Composable
    get() = (this.value / androidx.compose.ui.platform.LocalDensity.current.density).dp

val sfMediumFont = FontFamily(Font(R.font.sf_ui_display_medium))
val sfSemibold = FontFamily(Font(R.font.poppins_semibold))
val montserrat = FontFamily(Font(R.font.montserrat_regular))
val montserrat_medium = FontFamily(Font(R.font.montserrat_medium))
val montserrat_semibold = FontFamily(Font(R.font.montserrat_semibold))