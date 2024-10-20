package com.ruchitech.cashentery.ui.screens.mobile_auth

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ruchitech.cashentery.R
import com.ruchitech.cashentery.WebViewActivity
import com.ruchitech.cashentery.helper.privacyPolicy
import com.ruchitech.cashentery.helper.termsAndCond
import com.ruchitech.cashentery.ui.screens.mobile_auth.google_signin.SignInViewModel

@Composable
fun GoogleSignInUi(viewModel: SignInViewModel, onGoogleSignInClick: () -> Unit) {
    val context = LocalContext.current
    val loadingBar by viewModel.circularLoadingIndicator.collectAsState()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Center)
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Section: Logo or App Name
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(modifier = Modifier.height(100.dp))
                // Replace this with an actual logo if needed
                Icon(
                    painter = painterResource(id = R.drawable.app_icon), // Replace with your app logo
                    contentDescription = "Cash Entry",
                    modifier = Modifier.size(100.dp),
                    tint = Color.Unspecified // Keep original logo colors
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Welcome to Cash Entry",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color(0xFF333333)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Sign in to continue",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF777777)
                )
            }

            // Center Section: Google Sign-In Button
            Button(
                enabled = !loadingBar,
                onClick = { onGoogleSignInClick() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(0.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                elevation = ButtonDefaults.buttonElevation(8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFF4285F4), // Google blue
                                    Color(0xFF34A853), // Google green
                                    Color(0xFFFBBC05), // Google yellow
                                    Color(0xFFEA4335)  // Google red
                                )
                            )
                        )
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_google_logo), // Google icon
                        contentDescription = "Google",
                        tint = Color.Unspecified, // Ensure Google icon color stays intact
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Continue with Google",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.White
                    )
                }
            }

            // Bottom Section: Terms and Conditions
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "By continuing, you agree to our",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF777777)
                )
                Row {
                    TextButton(onClick = {
                        val intent = Intent(context, WebViewActivity::class.java)
                        intent.putExtra("url", privacyPolicy)
                        intent.putExtra("type", "Privacy Policy")
                        context.startActivity(intent)
                    }) {
                        Text(
                            text = "Privacy Policy",
                            color = Color.Blue,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    Text(
                        text = "and",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF777777)
                    )
                    TextButton(onClick = {
                        val intent = Intent(context, WebViewActivity::class.java)
                        intent.putExtra("url", termsAndCond)
                        intent.putExtra("type", "Terms & Conditions")
                        context.startActivity(intent)
                    }) {
                        Text(
                            text = "Terms of Service",
                            color = Color.Blue,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
        if (loadingBar) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.DarkGray.copy(alpha = 0.3F))
            ) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(80.dp)
                        .align(Alignment.Center)
                )
            }
        }
    }
}
