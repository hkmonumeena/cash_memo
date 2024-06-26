package com.ruchitech.cashentery.ui.screens.mobile_auth

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ruchitech.cashentery.ui.theme.montserrat_semibold
import com.ruchitech.cashentery.ui.theme.nonScaledSp
import com.ruchitech.cashentery.ui.theme.sfSemibold

@Composable
fun MobileAuthUi(viewModel: MobileAuthViewModel, onCodeSent: (verificationId: String?) -> Unit) {
    var amount by remember { mutableStateOf("+919131414139") }
    val state by viewModel.authState.collectAsState()
    LaunchedEffect(state) {
        Log.e("gkfdgjmldf", "Mobile Auth $state")
        when (state) {
            MobileAuthViewModel.AuthState.Authenticated -> {}
            MobileAuthViewModel.AuthState.CodeSent -> {
                onCodeSent(viewModel.verificationId)
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
            .background(Color.White), contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Mobile Number",
                fontFamily = montserrat_semibold,
                fontSize = 10.sp.nonScaledSp,
                color = Color(0xFF858585),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(5.dp))
            BasicTextField(
                value = amount,
                onValueChange = {
                    amount = it
                },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number, imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = {
                    amountFocus.freeFocus()
                }),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Gray.copy(alpha = 0.1f), shape = RoundedCornerShape(5.dp))
                    .padding(14.dp)
                    .focusRequester(amountFocus)
                    .onFocusChanged { },
                textStyle = TextStyle(
                    color = Color(0xFF323232),
                    fontFamily = montserrat_semibold,
                    fontSize = 12.sp.nonScaledSp
                )
            )
            Spacer(modifier = Modifier.height(15.dp))
            if (state is MobileAuthViewModel.AuthState.Loading) CircularProgressIndicator() else Button(
                onClick = {
                    viewModel.sendOtp(amount, context)
                }) {
                Text(text = "Get OTP", fontFamily = sfSemibold, fontSize = 12.sp.nonScaledSp)
            }
            Spacer(modifier = Modifier.height(15.dp))


        }
    }
}