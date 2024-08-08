package com.ruchitech.cashentery.ui.screens.mobile_auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ruchitech.cashentery.R
import com.ruchitech.cashentery.ui.screens.common_ui.SpacerHeight
import com.ruchitech.cashentery.ui.theme.MainBackgroundSurface
import com.ruchitech.cashentery.ui.theme.nonScaledSp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun VerifyOtpUi(
    viewModel: VerifyOtpViewModel,
    onAuthenticated: () -> Unit,
    verificationId: String?,
    mobileNumber: String?,
    onBack: () -> Unit,
) {
//    val state by viewModel.authState.collectAsState()
    LaunchedEffect(true) {
        viewModel.verificationId = verificationId
        viewModel.mobileNumber.value = mobileNumber?:""
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MainBackgroundSurface),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(150.dp))
            Image(
                painter = painterResource(id = R.drawable.ic_otp),
                contentDescription = "mobile icon",
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .border(2.dp, Color.White, shape = CircleShape)
                    .padding(10.dp)
            )
            Spacer(modifier = Modifier.height(30.dp))
            Text(
                text = "Verification",
                modifier = Modifier.fillMaxWidth(),
                fontSize = 24.sp.nonScaledSp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = Color(0xFF333333)
            )
            SpacerHeight(40)
            Text(
                text = "Please Enter 6 Digit Otp",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                fontSize = 16.sp.nonScaledSp,
                color = Color(0xFF666666)
            )
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
                ),
                singleLine = true,
                maxLines = 1
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 0.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                (0 until otpLength).map { index ->
                    viewModel.filledOtp.value = editValue
                    OtpCell(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(1.dp))
                            .background(color = MaterialTheme.colorScheme.surface)
                            .border(1.dp, Color.DarkGray, shape = RoundedCornerShape(1.dp))
                            .onFocusChanged {
                                focusRequester.requestFocus()
                            }
                            .clickable {
                                keyboard?.show()
                            },
                        value = editValue.getOrNull(index)?.toString() ?: "",
                        isCursorVisible = editValue.length == index
                    )
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
                Text("Verify Otp", color = Color.White, fontSize = 18.sp)
            }
        }
        if (viewModel.showLoading.value) {
            onAuthenticated()
            viewModel.showLoading.value = false
        }
        IconButton(
            onClick = {
                onBack()
            }, modifier = Modifier
                .padding(16.dp)
                .background(Color(0xFFFFA500).copy(0.5F), shape = CircleShape)
                .align(Alignment.TopStart)
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowLeft,
                contentDescription = null,
                modifier = Modifier.size(50.dp)
            )
        }

    }
}

@Composable
fun OtpCell(
    modifier: Modifier,
    value: String,
    isCursorVisible: Boolean = false,
) {
    val cursorSymbol = remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    LaunchedEffect(key1 = isCursorVisible) {
        if (isCursorVisible) {
            scope.launch {
                delay(350)
                cursorSymbol.value = if (cursorSymbol.value.isEmpty()) "|" else ""
            }
        }
    }

    Box(
        modifier = modifier
            .background(color = MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(8.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            text = if (isCursorVisible) cursorSymbol.value else value,
            style = MaterialTheme.typography.headlineSmall.copy(
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}