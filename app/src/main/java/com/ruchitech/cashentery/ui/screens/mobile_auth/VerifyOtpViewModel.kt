package com.ruchitech.cashentery.ui.screens.mobile_auth

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.ruchitech.cashentery.MainActivity
import com.ruchitech.cashentery.helper.sharedpreference.AppPreference
import com.ruchitech.cashentery.helper.toast.MyToast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class VerifyOtpViewModel @Inject constructor(
    private val myToast: MyToast,
     val appPreference: AppPreference,
) : ViewModel() {
    val showLoading = mutableStateOf(false)

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    var verificationId: String? = null

    var mobileNumber = mutableStateOf("")
    val filledOtp = mutableStateOf("")



    fun validationCheck() {
        if (filledOtp.value.length < 6) {
            myToast.showToast("कृपया 6 अंकों का ओटीपी दर्ज करें")
            return
        } else {
            verifyOtp(filledOtp.value)
        }
    }

    fun sendOtp(phoneNumber: String, context: Context) {
        _authState.value = AuthState.Loading

        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(context as MainActivity)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    signInWithPhoneAuthCredential(credential)
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    _authState.value = AuthState.Error(e.message ?: "Verification failed")
                }

                override fun onCodeSent(
                    verificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken,
                ) {
                    this@VerifyOtpViewModel.verificationId = verificationId
                    _authState.value = AuthState.CodeSent

                }
            })
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    fun verifyOtp(otp: String) {
        val credential = verificationId?.let { PhoneAuthProvider.getCredential(it, otp) }
        if (credential != null) {
            signInWithPhoneAuthCredential(credential)
        } else {
            _authState.value = AuthState.Error("Verification ID is null")
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _authState.value = AuthState.Authenticated
                    appPreference.userId = auth.uid
                    appPreference.isUserLoggedIn = true
                    showLoading.value = true
                } else {
                    _authState.value =
                        AuthState.Error(task.exception?.message ?: "Authentication failed")
                }
            }
    }

    sealed class AuthState {
        object Idle : AuthState()
        object Loading : AuthState()
        object CodeSent : AuthState()
        object Authenticated : AuthState()
        data class Error(val message: String) : AuthState()
    }

}