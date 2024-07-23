package com.ruchitech.cashentery.ui.screens.common_ui

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SpacerHeight(value:Int=0) {
    Spacer(modifier = Modifier.height(value.dp))
}
@Composable
fun SpacerWidth(value:Int=0) {
    Spacer(modifier = Modifier.width(value.dp))
}