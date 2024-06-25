package com.ruchitech.cashentery.helper.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.ruchitech.cashentery.ui.screens.add_transactions.AddTransactionUi
import com.ruchitech.cashentery.ui.screens.add_transactions.AddTransactionViewModel
import com.ruchitech.cashentery.ui.screens.mobile_auth.MobileAuthUi
import com.ruchitech.cashentery.ui.screens.mobile_auth.MobileAuthViewModel
import com.ruchitech.cashentery.ui.screens.mobile_auth.VerifyOtpUi

@Composable
fun NavigationComponent(
    navHostController: NavHostController,
    snackbarHostState: SnackbarHostState,
    paddingValues: PaddingValues,
    onMenuClick: () -> Unit,
) {
    NavHost(
        navController = navHostController,
        startDestination = Screen.MobileAuth,
        modifier = Modifier.padding(paddingValues)
    ) {
        composable<Screen.MobileAuth> {
            val viewModel = hiltViewModel<MobileAuthViewModel>()
            MobileAuthUi(viewModel, onCodeSent = {
                navHostController.navigate(Screen.VerifyOtp(it))
            })
        }

        composable<Screen.VerifyOtp> {
            val viewModel = hiltViewModel<MobileAuthViewModel>()
            val args = it.toRoute<Screen.VerifyOtp>()
            VerifyOtpUi(viewModel, onAuthenticated = {
                navHostController.navigate(Screen.AddTransaction)
            },args.verificationId)
        }

        composable<Screen.AddTransaction> {
            val viewModel = hiltViewModel<AddTransactionViewModel>()
            AddTransactionUi(viewModel)
        }
    }
}