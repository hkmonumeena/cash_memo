package com.ruchitech.cashentery.ui.screens.mobile_auth

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ruchitech.cashentery.R
import com.ruchitech.cashentery.WebViewActivity
import com.ruchitech.cashentery.helper.privacyPolicy
import com.ruchitech.cashentery.helper.termsAndCond
import com.ruchitech.cashentery.ui.screens.common_ui.PrivacyPolicyText
import com.ruchitech.cashentery.ui.screens.common_ui.SpacerHeight
import com.ruchitech.cashentery.ui.screens.common_ui.TermsAndConditionsText
import com.ruchitech.cashentery.ui.theme.MainBackgroundSurface
import com.ruchitech.cashentery.ui.theme.nonScaledSp

@Composable
fun MobileAuthUi(
    viewModel: MobileAuthViewModel,
    onCodeSent: (verificationId: String?, mobileNumber: String?) -> Unit,
) {
    var amount by remember { mutableStateOf("+91") }
    val state by viewModel.authState.collectAsState()
    LaunchedEffect(state) {
        when (state) {
            MobileAuthViewModel.AuthState.Authenticated -> {}
            MobileAuthViewModel.AuthState.CodeSent -> {
                onCodeSent(viewModel.verificationId, amount)
                viewModel.changeState()
            }

            is MobileAuthViewModel.AuthState.Error -> {}
            MobileAuthViewModel.AuthState.Idle -> {}
            MobileAuthViewModel.AuthState.Loading -> {}
        }
    }

    val amountFocus = remember {
        FocusRequester()
    }
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MainBackgroundSurface),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .fillMaxHeight(0.65F),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {

            Image(
                painter = painterResource(id = R.drawable.ic_otp),
                contentDescription = null,
                modifier = Modifier.size(120.dp)
            )

            SpacerHeight(40)

            Text(
                text = "An OTP will be sent to your mobile number for verification. Please ensure your number is correct and accessible.",
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                color = Color(0xFF333333),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp)
            )
            SpacerHeight(45)

            Text(
                text = "Mobile Number",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 14.sp.nonScaledSp,
                color = Color(0xFF333333),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            TextField(
                value = amount,
                onValueChange = {
                    amount = it
                },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = {
                    amountFocus.freeFocus()
                }),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(60.dp)
                    .background(Color(0xFFF1F1F1), shape = RoundedCornerShape(10.dp))
                    .focusRequester(amountFocus)
                    .onFocusChanged { },
                textStyle = TextStyle(
                    color = Color(0xFF333333),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp.nonScaledSp
                ),
                placeholder = {
                    Text(
                        text = "Enter your mobile number here...", style = TextStyle(
                            color = Color.Gray,
                            fontSize = 16.sp.nonScaledSp
                        )
                    )
                },
                singleLine = true,
                maxLines = 1,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )
            Spacer(modifier = Modifier.height(20.dp))
            if (state is MobileAuthViewModel.AuthState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.primary
                )
            } else {
                Button(
                    onClick = {
                        viewModel.sendOtp(amount, context)
                    },
                    modifier = Modifier
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(
                        text = "Get OTP",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        color = Color.White
                    )
                }
            }
            Spacer(modifier = Modifier.height(40.dp))
            TermsAndConditionsText(onTermsClicked = {
                val intent = Intent(context, WebViewActivity::class.java)
                intent.putExtra("url", termsAndCond)
                intent.putExtra("type", "Terms & Conditions")
                context.startActivity(intent)
            })
            Spacer(modifier = Modifier.height(5.dp))
            PrivacyPolicyText(onPrivacyPolicyClicked = {
                val intent = Intent(context, WebViewActivity::class.java)
                intent.putExtra("url", privacyPolicy)
                intent.putExtra("type", "Privacy Policy")
                context.startActivity(intent)
            })
            /*        Text(
                        text = "To verify your identity, we will send a One-Time Password (OTP) to the mobile number you entered. Please ensure that the number is correct and accessible. The OTP will be used for security purposes and to complete the verification process.",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp.nonScaledSp,
                            color = Color(0xFF333333),
                            lineHeight = 20.sp // Add this to increase the line spacing
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    )*/

        }
    }
}