package com.ruchitech.cashentery.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ruchitech.cashentery.R
import com.ruchitech.cashentery.helper.navigation.Screen
import com.ruchitech.cashentery.ui.screens.common_ui.SpacerHeight
import com.ruchitech.cashentery.ui.theme.MainBackgroundSurface
import com.ruchitech.cashentery.ui.theme.nonScaledSp
import com.ruchitech.cashentery.ui.theme.sfSemibold

@Composable
fun SplashUi(viewModel: SplashViewModel, onNavigate: (screen: Screen) -> Unit) {
    val isInitialized by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(isInitialized) {
        viewModel.checkUserLoggedIn {
            onNavigate(it)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MainBackgroundSurface),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            Image(
                painter = painterResource(id = R.drawable.app_icon),
                contentDescription = null,
                modifier = Modifier.size(120.dp)
            )
            SpacerHeight(20)
            Text(text = "Cash Entry", fontSize = 18.sp.nonScaledSp, fontFamily = sfSemibold)
        }
    }
}