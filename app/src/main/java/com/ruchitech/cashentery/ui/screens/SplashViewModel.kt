package com.ruchitech.cashentery.ui.screens

import android.os.Handler
import androidx.lifecycle.viewModelScope
import com.ruchitech.cashentery.helper.SharedViewModel
import com.ruchitech.cashentery.helper.navigation.Screen
import com.ruchitech.cashentery.helper.sharedpreference.AppPreference
import com.ruchitech.cashentery.helper.toast.MyToast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val appPreference: AppPreference,
    private val myToast: MyToast,
) : SharedViewModel() {



    fun checkUserLoggedIn(navigateTo: (screen: Screen) -> Unit) {
        viewModelScope.launch {
            Handler().postDelayed({
                if (appPreference.isUserLoggedIn) {
                    navigateTo(Screen.Home)
                } else {
                    navigateTo(Screen.SignInGoogle)
                }
            }, 1500)
        }

    }

}