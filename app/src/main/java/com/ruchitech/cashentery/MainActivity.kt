package com.ruchitech.cashentery

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.ads.MobileAds
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
                Log.e("fdjgfklgfg", "onCreate: ${currentDestination}")
                val context = LocalContext.current
                val showExitDialog = remember { mutableStateOf(false) } // Track dialog state
                Scaffold(modifier = Modifier.fillMaxSize(),
                    floatingActionButton = {
                        if (!hideTransactionButton(currentDestination)) {
                            FloatingActionButton(
                                onClick = {
                                    navController.navigate(Screen.AddTransaction(type = 2))
                                },
                                modifier = Modifier
                                    .size(72.dp) // Larger size for a more prominent loosk
                                    .shadow(
                                        12.dp,
                                        shape = RoundedCornerShape(20.dp)
                                    ), // Add shadow with custom shape
                                shape = RoundedCornerShape(20.dp), // Use a rounded rectangle shape
                                elevation = FloatingActionButtonDefaults.elevation(12.dp), // Increased elevation for depth
                                containerColor = Color(0xFFff4081),
                                contentColor = Color.White // White icon for better contrast
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Add Transaction",
                                    modifier = Modifier.size(36.dp), // Larger icon size
                                    tint = Color.White // Icon color
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
                        this,
                        navHostController = navController,
                        snackbarHostState = snackbarHostState,
                        paddingValues = innerPadding
                    ) {}
                    // Exit confirmation dialog
                    if (showExitDialog.value) {
                        AlertDialog(
                            onDismissRequest = { showExitDialog.value = false },
                            title = { Text(text = "Exit App") },
                            text = { Text(text = "Are you sure you want to exit?") },
                            confirmButton = {
                                Button(onClick = {
                                    showExitDialog.value = false
                                    (context as? Activity)?.finish() // Close the app
                                }) {
                                    Text("Exit")
                                }
                            },
                            dismissButton = {
                                Button(onClick = { showExitDialog.value = false }) {
                                    Text("Cancel")
                                }
                            }
                        )
                    }

                    BackHandler(enabled = true) {
                        if (navController.previousBackStackEntry != null) {
                            navController.popBackStack()
                        } else {
                            showExitDialog.value = true // Show exit dialog if no more backstack entries
                        }
                    }

                }
            }
        }
    }
}


@Composable
fun CashInOutButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .padding(horizontal = 16.dp)
            .height(48.dp)
            .fillMaxWidth(),
        elevation = ButtonDefaults.buttonElevation(8.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Text(
            text = text,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}