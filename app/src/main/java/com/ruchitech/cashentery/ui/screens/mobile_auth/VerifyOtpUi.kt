package com.ruchitech.cashentery.ui.screens.mobile_auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ruchitech.cashentery.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun VerifyOtpUi(
    viewModel: MobileAuthViewModel,
    onAuthenticated: () -> Unit,
    verificationId: String?,
) {
    val state by viewModel.authState.collectAsState()
    viewModel.verificationId = verificationId
    when (state) {
        MobileAuthViewModel.AuthState.Authenticated -> {
            onAuthenticated()
        }

        MobileAuthViewModel.AuthState.CodeSent -> {
            //onCodeSent(viewModel.verificationId)
        }

        is MobileAuthViewModel.AuthState.Error -> {}
        MobileAuthViewModel.AuthState.Idle -> {}
        MobileAuthViewModel.AuthState.Loading -> {}
    }
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(horizontal = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_background),
                contentDescription = "mobile icon",
                modifier = Modifier
                    .fillMaxHeight(0.3F)
                    .padding(10.dp)
            )
            Spacer(modifier = Modifier.height(30.dp))
            Text(
                text = "Verification", modifier = Modifier.fillMaxWidth(),
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Please Enter 6 Digit Otp",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp),
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(10.dp))
            val (editValue, setEditValue) = remember { mutableStateOf("") }
            val otpLength = remember { 6 }
            val focusRequester = remember { FocusRequester() }

            val keyboard = LocalSoftwareKeyboardController.current
            TextField(
                value = editValue,
                onValueChange = {
                    if (it.length <= otpLength) {
                        setEditValue(it)
                    }
                },
                modifier = Modifier
                    .size(0.dp)
                    .focusRequester(focusRequester),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number
                )
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                (0 until otpLength).map { index ->
                    viewModel.filledOtp.value = editValue
                    OtpCell(
                        modifier = Modifier
                            .size(45.dp)
                            .onFocusChanged {
                                focusRequester.requestFocus()
                            }
                            .clickable {
                                //   focusRequester.requestFocus()
                                keyboard?.show()
                            }
                            .border(1.dp, Color.DarkGray),
                        value = editValue.getOrNull(index)?.toString() ?: "",
                        isCursorVisible = editValue.length == index
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                }
            }

            Spacer(modifier = Modifier.height(25.dp))
            Button(
                onClick = {
                    viewModel.validationCheck()
                },
                modifier = Modifier
                    .height(56.dp)
                    .shadow(shape = RoundedCornerShape(15.dp), elevation = 0.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA500))
            ) {
                Text("Verify Otp", color = Color.White)
            }
        }
        if (viewModel.showLoading.value) {
            CircularProgressIndicator()
        }
    }

}

@Composable
fun OtpCell(
    modifier: Modifier,
    value: String,
    isCursorVisible: Boolean = false,
) {
    val scope = rememberCoroutineScope()
    val (cursorSymbol, setCursorSymbol) = remember { mutableStateOf("") }

    LaunchedEffect(key1 = cursorSymbol, isCursorVisible) {
        if (isCursorVisible) {
            scope.launch {
                delay(350)
                setCursorSymbol(if (cursorSymbol.isEmpty()) "|" else "")
            }
        }
    }

    Box(
        modifier = modifier
    ) {

        Text(
            text = if (isCursorVisible) cursorSymbol else value,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}