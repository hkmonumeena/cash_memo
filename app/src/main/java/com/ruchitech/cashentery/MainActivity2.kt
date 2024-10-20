package com.ruchitech.cashentery

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ruchitech.cashentery.ui.screens.mobile_auth.google_signin.SignInViewModel
import com.ruchitech.cashentery.ui.theme.CashEnteryTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity2 : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CashEnteryTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    App()
                }
            }
        }
    }
}

@Composable
fun App() {

    val navController = rememberNavController()
    val context = LocalContext.current
    val googleSignInViewModel = hiltViewModel<SignInViewModel>()

    NavHost(navController = navController, startDestination = "home") {

        composable(route = "home") {
            HomeScreen {
                googleSignInViewModel.handleGoogleSignIn(context, onSuccess = {

                })
            }
        }

        composable(route = "profile") {
            Text(text = "profile")
        }

    }

}


@Composable
fun HomeScreen(onGoogleSignInClick: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        Button(
            onClick = {
                onGoogleSignInClick()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .align(Alignment.Center)
                .padding(5.dp),
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(0.dp),
        ) {
            Row(
                modifier = Modifier
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color.Red,
                                Color.Yellow,
                            ),
                        )
                    )
                    .fillMaxSize()
                    .padding(10.dp),
                // contentAlignment = Alignment.Center
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_rupay),
                    contentDescription = "Google"
                )
                Text(
                    modifier = Modifier.padding(start = 10.dp),
                    text = "Continue with Google"
                )
            }
        }
    }
}