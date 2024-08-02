package com.ruchitech.cashentery

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.initialization.InitializationStatus
import com.ruchitech.cashentery.helper.hideTransactionButton
import com.ruchitech.cashentery.helper.navigation.NavigationComponent
import com.ruchitech.cashentery.helper.navigation.Screen
import com.ruchitech.cashentery.ui.screens.common_ui.BannerAdView
import com.ruchitech.cashentery.ui.theme.CashEnteryTheme
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    var lastTagUsed: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MobileAds.initialize(this) { }
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                resources.getColor(R.color.theme_color),
                resources.getColor(R.color.black)
            )
        )
        setContent {
            CashEnteryTheme {
                val navController = rememberNavController()
                val snackbarHostState = remember { SnackbarHostState() }
                val currentBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = currentBackStackEntry?.destination?.route
                Scaffold(modifier = Modifier.fillMaxSize(),
                    floatingActionButton = {
                        if (!hideTransactionButton(currentDestination)) {
                            FloatingActionButton(onClick = {
                                navController.navigate(Screen.AddTransaction)
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Add Transaction"
                                )
                            }
                        }
                    },
                    floatingActionButtonPosition = FabPosition.End,
                    bottomBar = {}) { innerPadding ->
                    BannerAdView(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(innerPadding)
                            .padding(16.dp),
                        adUnitId = "ca-app-pub-1120363743713276/7875099206"
                    )
                    NavigationComponent(
                        navHostController = navController,
                        snackbarHostState = snackbarHostState,
                        paddingValues = innerPadding
                    ) {}
                }
            }
        }
    }
}
