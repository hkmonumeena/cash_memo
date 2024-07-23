package com.ruchitech.cashentery.helper.navigation

import android.util.Log
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.google.gson.Gson
import com.ruchitech.cashentery.ui.screens.add_transactions.AddTransactionUi
import com.ruchitech.cashentery.ui.screens.add_transactions.AddTransactionViewModel
import com.ruchitech.cashentery.ui.screens.chatview.TransactionDetailsUi
import com.ruchitech.cashentery.ui.screens.chatview.TransactionDetailsViewModel
import com.ruchitech.cashentery.ui.screens.home.HomeUi
import com.ruchitech.cashentery.ui.screens.home.HomeViewModel
import com.ruchitech.cashentery.ui.screens.mobile_auth.MobileAuthUi
import com.ruchitech.cashentery.ui.screens.mobile_auth.MobileAuthViewModel
import com.ruchitech.cashentery.ui.screens.mobile_auth.VerifyOtpUi
import com.ruchitech.cashentery.ui.screens.mobile_auth.VerifyOtpViewModel
import com.ruchitech.cashentery.ui.screens.transactions.TransactionUi
import com.ruchitech.cashentery.ui.screens.transactions.TransactionsViewModel
import java.util.Date

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
            if (viewModel.appPreference.isUserLoggedIn) {
                navHostController.navigate(Screen.Home) {
                    popUpTo(Screen.MobileAuth) {
                        inclusive = true
                    }
                }
                return@composable
            }
            MobileAuthUi(viewModel, onCodeSent = {
                navHostController.navigate(Screen.VerifyOtp(it))
            })
        }

        composable<Screen.VerifyOtp> {
            val viewModel = hiltViewModel<VerifyOtpViewModel>()
            val args = it.toRoute<Screen.VerifyOtp>()
            VerifyOtpUi(viewModel, onAuthenticated = {
                navHostController.navigate(Screen.Home) {
                    popUpTo(Screen.MobileAuth) {
                        inclusive = true
                    }
                }
            }, args.verificationId)
        }

        composable<Screen.AddTransaction> {
            val viewModel = hiltViewModel<AddTransactionViewModel>()
            AddTransactionUi(viewModel, onSuccess = {
                navHostController.popBackStack()
            }, onBack = {
                navHostController.popBackStack()
            })

        }
        composable<Screen.Home> {
            val viewModel = hiltViewModel<HomeViewModel>()
            HomeUi(viewModel, navigateToAddTransaction = {
                navHostController.navigate(Screen.AddTransaction)

            }, navigateToTransactions = {
                navHostController.navigate(Screen.Transactions)
            }, navigateToDetails = {
                navHostController.navigate(Screen.TransactionDetails(Gson().toJson(it)))
            }, onSignOut = {
                navHostController.navigate(Screen.MobileAuth) {
                    popUpTo(Screen.Home) {
                        inclusive = true
                    }
                }
            })
        }

        composable<Screen.Transactions> {
            val viewModel = hiltViewModel<TransactionsViewModel>()
            Log.e("fdghdfhgfdh", "NavigationComponent: 101", )
            TransactionUi(viewModel, onBack = {
                navHostController.popBackStack()
            })
        }



        composable<Screen.TransactionDetails> {
            val viewModel = hiltViewModel<TransactionDetailsViewModel>()
            val args = it.toRoute<Screen.TransactionDetails>()
            var test by rememberSaveable {
                mutableStateOf("def")
            }
            if (test == "def") {
                viewModel.getTransactionDetails(args.transactions)
                test = Date().toString()
            }
            TransactionDetailsUi(viewModel, onBack = {
                navHostController.popBackStack()
            }, onSuccess = {

            })
        }
    }
}