package com.ruchitech.cashentery.helper.navigation

import android.util.Log
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.ruchitech.cashentery.MainActivity
import com.ruchitech.cashentery.ui.screens.SplashUi
import com.ruchitech.cashentery.ui.screens.SplashViewModel
import com.ruchitech.cashentery.ui.screens.add_transactions.AddTransactionUi
import com.ruchitech.cashentery.ui.screens.add_transactions.AddTransactionViewModel
import com.ruchitech.cashentery.ui.screens.chatview.TransactionDetailsUi
import com.ruchitech.cashentery.ui.screens.chatview.TransactionDetailsViewModel
import com.ruchitech.cashentery.ui.screens.home.HomeUi
import com.ruchitech.cashentery.ui.screens.home.HomeViewModel
import com.ruchitech.cashentery.ui.screens.mobile_auth.GoogleSignInUi
import com.ruchitech.cashentery.ui.screens.mobile_auth.MobileAuthUi
import com.ruchitech.cashentery.ui.screens.mobile_auth.MobileAuthViewModel
import com.ruchitech.cashentery.ui.screens.mobile_auth.VerifyOtpUi
import com.ruchitech.cashentery.ui.screens.mobile_auth.VerifyOtpViewModel
import com.ruchitech.cashentery.ui.screens.mobile_auth.google_signin.SignInViewModel
import com.ruchitech.cashentery.ui.screens.settings.SettingsUi
import com.ruchitech.cashentery.ui.screens.settings.SettingsViewModel
import com.ruchitech.cashentery.ui.screens.transactions.TransactionUi
import com.ruchitech.cashentery.ui.screens.transactions.TransactionsViewModel

private fun navigateTo(
    navController: NavHostController,
    screen: Screen,
    popUpToCurrent: Boolean = false,
) {
    navController.navigate(screen) {
        if (popUpToCurrent) {
            popUpTo(screen) { inclusive = true }
        }
    }
}


@Composable
fun NavigationComponent(
    mainActivity: MainActivity,
    navHostController: NavHostController,
    snackbarHostState: SnackbarHostState,
    paddingValues: PaddingValues,
    onMenuClick: () -> Unit,
) {
    NavHost(
        navController = navHostController,
        startDestination = Screen.SplashScreen,
        modifier = Modifier.padding(paddingValues),
        enterTransition = {
            slideInHorizontally(
                initialOffsetX = { 1000 }, // Slide from the right
                animationSpec = tween(500)
            ) + fadeIn(animationSpec = tween(500))
        },
        popEnterTransition = {
            slideInHorizontally(
                initialOffsetX = { -1000 }, // Slide from the left
                animationSpec = tween(500)
            ) + fadeIn(animationSpec = tween(500))
        },
        popExitTransition = {
            slideOutHorizontally(
                targetOffsetX = { 1000 }, // Slide to the right
                animationSpec = tween(500)
            ) + fadeOut(animationSpec = tween(500))
        }
    ) {

        composable<Screen.SplashScreen> {
            val viewModel = hiltViewModel<SplashViewModel>()
            SplashUi(viewModel, onNavigate = { screen ->
                Log.e("kijuhygtfr6d", "NavigationComponent: ${screen}")
                navHostController.navigate(screen) {
                    popUpTo(Screen.SplashScreen) { inclusive = true }
                }
            })

        }


        composable<Screen.MobileAuth> {
            val viewModel = hiltViewModel<MobileAuthViewModel>()
            if (viewModel.appPreference.isUserLoggedIn) {
                // viewModel.appPreference.userId = "W5mzbR4YFSTClH6Tsf28LilEH9d2"
                navHostController.navigate(Screen.Home) {
                    popUpTo(Screen.MobileAuth) { inclusive = true }
                }
            } else {
                MobileAuthUi(
                    viewModel = viewModel,
                    onCodeSent = { code, mobileNumber ->
                        navHostController.navigate(Screen.VerifyOtp(code, mobileNumber))
                    }
                )
            }
        }

        composable<Screen.SignInGoogle> {
            val context = LocalContext.current
            val viewModel = hiltViewModel<SignInViewModel>()
            var isInitialized by rememberSaveable { mutableStateOf(false) }
            Log.e("okoiujhgfggio", "NavigationComponent: working")
            /*        if (viewModel.appPreference.isUserLoggedIn) {
                        Log.e("okoiujhgfggio", "NavigationComponent: loggesdin")
                        navHostController.navigate(Screen.Home) {
                            popUpTo(Screen.SignInGoogle) { inclusive = true }
                        }
                    }*/

            GoogleSignInUi(viewModel, onGoogleSignInClick = {
                viewModel.handleGoogleSignIn(context, onSuccess = {
                    navHostController.navigate(Screen.Home) {
                        popUpTo(Screen.SignInGoogle) { inclusive = true }
                    }
                })
            })


//            if (!isInitialized) {
//                viewModel.getData(args.transactions)
//                isInitialized = true
//            }

        }

        composable<Screen.VerifyOtp> {
            val viewModel = hiltViewModel<VerifyOtpViewModel>()
                val args = it.toRoute<Screen.VerifyOtp>()
            VerifyOtpUi(
                onBack = {
                    navHostController.popBackStack()
                },
                viewModel = viewModel,
                onAuthenticated = {
                    navHostController.navigate(Screen.Home) {
                        popUpTo(Screen.MobileAuth) { inclusive = true }
                    }
                },
                verificationId = args.verificationId,
                mobileNumber = args.mobileNumber
            )
        }

        composable<Screen.AddTransaction> {
            val viewModel = hiltViewModel<AddTransactionViewModel>()
            val args = it.toRoute<Screen.AddTransaction>()
            AddTransactionUi(
                args.type,
                viewModel = viewModel,
                onSuccess = {
                    navHostController.popBackStack()
                },
                onBack = {
                    navHostController.popBackStack()
                }
            )
        }

        composable<Screen.Home> {
            val viewModel = hiltViewModel<HomeViewModel>()
            HomeUi(
                viewModel = viewModel,
                navigateToAddTransaction = {
                    navigateTo(navHostController, Screen.AddTransaction(type = it))
                },
                navigateToTransactions = {
                    navigateTo(navHostController, Screen.Transactions)
                },
                navigateToDetails = { transaction ->
                    // val transactionJson = Gson().toJson(transaction)
                    navigateTo(navHostController, Screen.TransactionDetails(transaction))
                },
                navigateToSettings = {
                    navigateTo(navHostController, Screen.Settings, false)
                },
                onSignOut = {
                    navHostController.navigate(Screen.SignInGoogle) {
                        popUpTo(Screen.Home) { inclusive = true }
                    }
                }
            )
        }

        composable<Screen.Transactions> {
            val viewModel = hiltViewModel<TransactionsViewModel>()
            TransactionUi(
                viewModel = viewModel,
                onBack = {
                    navHostController.popBackStack()
                }
            )
        }

        composable<Screen.Settings> {
            val viewModel = hiltViewModel<SettingsViewModel>()
            SettingsUi(
                viewModel,
                onBack = { navHostController.popBackStack() }
            )
        }

        composable<Screen.TransactionDetails> {
            val viewModel = hiltViewModel<TransactionDetailsViewModel>()
            val args = it.toRoute<Screen.TransactionDetails>()
            var isInitialized by rememberSaveable { mutableStateOf(false) }

            if (!isInitialized) {
                viewModel.getData(args.transactions)
                isInitialized = true
            }

            TransactionDetailsUi(
                viewModel = viewModel,
                onBack = {
                    navHostController.popBackStack()
                },
                onSuccess = {
                    // Handle success
                }
            )
        }
    }
}
