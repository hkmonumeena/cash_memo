package com.ruchitech.cashentery.ui.screens.mobile_auth.google_signin

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.ruchitech.cashentery.R
import com.ruchitech.cashentery.helper.SharedViewModel
import com.ruchitech.cashentery.helper.sharedpreference.AppPreference
import com.ruchitech.cashentery.helper.toast.MyToast
import com.ruchitech.cashentery.retrofit.remote.Status
import com.ruchitech.cashentery.retrofit.repository.AccountRepository
import com.ruchitech.cashentery.ui.screens.mobile_auth.User
import com.ruchitech.cashentery.ui.screens.mobile_auth.data.CreateUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.security.MessageDigest
import java.util.UUID
import javax.inject.Inject

data class User(
    var id: String = "",
    val name: String = "",
    val photoUrl: String = "",
    val email: String = "",
    val country: String = "",
    val subscription: String = "",
)


@HiltViewModel
class SignInViewModel @Inject constructor(
    private val myToast: MyToast,
    val appPreference: AppPreference,
    private val accountRepository: AccountRepository,
) : SharedViewModel() {

    private val user = MutableLiveData<User>(null)

    // Function to handle Google Sign-In
    fun handleGoogleSignIn(context: Context, onSuccess: () -> Unit) {
        viewModelScope.launch {
            var auth = FirebaseAuth.getInstance()
            auth.signOut()
            appPreference.isUserLoggedIn = false
            appPreference.categoriesList = emptyList()
            // Collect the result of the Google Sign-In process
            googleSignIn(context).collect { result ->
                result.fold(
                    onSuccess = { authResult ->
                        // Handle successful sign-in
                        val currentUser = authResult.user
                        if (currentUser != null) {
                            Log.e("kljhgufy", "handleGoogleSignIn: ")
                            user.value = User(
                                currentUser.uid,
                                currentUser.displayName!!,
                                currentUser.photoUrl.toString(),
                                currentUser.email!!,
                                "India",
                                "Free"
                            )
                            appPreference.userData = makeUserData(
                                currentUser.uid,
                                currentUser.displayName ?: generateRandomUsername(),
                                currentUser.email ?: generateRandomUsername()
                            )
                            createUser {
                                Log.e("iiujhguimk", "handleGoogleSignIn: onSuccessCalled")
                                appPreference.mobileNumber = currentUser.email
                                appPreference.userId = currentUser.uid
                                appPreference.currentMonthOnly = true
                                appPreference.isUserLoggedIn = true
                                onSuccess()
                            }

                        }
                    },
                    onFailure = { e ->
                        // Handle sign-in error
                        myToast.showToast("Something went wrong: ${e.message}")
                        Log.d("Issue", "handleGoogleSignIn: ${e.message}")
                    }
                )
            }
        }
    }

    // Function to perform Google Sign-In and return a Flow of AuthResult
    private suspend fun googleSignIn(context: Context): Flow<Result<AuthResult>> {
        // Initialize Firebase Auth instance
        val firebaseAuth = FirebaseAuth.getInstance()

        // Return a Flow that emits the result of the Google Sign-In process
        return callbackFlow {
            try {
                // Initialize Credential Manager
                val credentialManager: CredentialManager = CredentialManager.create(context)

                // Generate a nonce (a random number used once) for security
                val ranNonce: String = UUID.randomUUID().toString()
                val bytes: ByteArray = ranNonce.toByteArray()
                val md: MessageDigest = MessageDigest.getInstance("SHA-256")
                val digest: ByteArray = md.digest(bytes)
                val hashedNonce: String = digest.fold("") { str, it -> str + "%02x".format(it) }

                // Set up Google ID option with necessary parameters
                val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false) // To give the user the option to choose from any Google account on their device, not just the ones they've used with your app before.
                    .setServerClientId(context.getString(R.string.web_client_id)) // This is required to identify the app on the backend server.
                    .setNonce(hashedNonce) // A nonce is a unique, random string used to ensure that the ID token received is fresh and to prevent replay attacks.
                    .setAutoSelectEnabled(true) // Which allows the user to be automatically signed in without additional user interaction if there is a single eligible account.
                    .build()

                // Create a credential request with the Google ID option
                val request: GetCredentialRequest = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()

                // Get the credential result from the Credential Manager
                val result = credentialManager.getCredential(context, request)
                val credential = result.credential

                // Check if the received credential is a valid Google ID Token
                if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    // Extract the Google ID Token credential
                    val googleIdTokenCredential =
                        GoogleIdTokenCredential.createFrom(credential.data)
                    // Create an auth credential using the Google ID Token
                    val authCredential =
                        GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)
                    // Sign in with Firebase using the auth credential
                    val authResult = firebaseAuth.signInWithCredential(authCredential)
                        .await() // .await() -> allows the coroutine to wait for the result of the authentication operation before proceeding.
                    // Send the successful result
                    trySend(Result.success(authResult)) // Is used to send the result of the Firebase sign-in operation to the Flow's collectors.
                } else {
                    // Throw an exception if the credential type is invalid
                    throw RuntimeException("Received an invalid credential type.")
                }

            } catch (e: GetCredentialCancellationException) {
                // Handle sign-in cancellation
                Log.e("utrhbgftyrhg", "googleSignIn: ${e.message}")
                trySend(Result.failure(Exception("Sign-in was canceled.")))
            } catch (e: Exception) {
                // Handle other exceptions
                trySend(Result.failure(e))
            }

            // When a collector starts collecting from the callbackFlow, the flow remains open and ready to emit values until the awaitClose block is reached or the flow is cancelled.
            // Even though the current block is empty, in other scenarios, you might use the awaitClose block to unregister listeners or release resources associated with the callback-based API.
            awaitClose { }
        }
    }


    private fun createUser(onSuccess: () -> Unit) {
        viewModelScope.launch {
            accountRepository.createUserApi(appPreference.userData!!).distinctUntilChanged()
                .collectLatest { response ->
                    when (response.status) {
                        Status.INITIAL -> Unit
                        Status.EMPTY -> Unit
                        Status.SUCCESS -> {
                            myToast.showToast("Sign in successfully!")
                            hideLoading()
                            onSuccess()
                        }

                        Status.ERROR -> {
                            hideLoading()
                        }

                        Status.LOADING -> {
                            showLoading()
//                            showLoading.value = true
                        }
                    }
                }
        }
    }

    fun generateRandomUsername(): String {
        val adjectives = listOf(
            "Swift", "Brave", "Mighty", "Silent", "Furious",
            "Cunning", "Lone", "Stealthy", "Wild", "Vigilant"
        )

        val nouns = listOf(
            "Tiger", "Wolf", "Eagle", "Panther", "Falcon",
            "Ninja", "Shadow", "Ranger", "Knight", "Assassin"
        )

        val numbers = (0..9999).toList() // Numbers from 0 to 9999

        val specialCharacters = listOf(
            "!", "@", "#", "$", "%", "&", "*"
        )

        val randomAdjective = adjectives.random()
        val randomNoun = nouns.random()
        val randomNumber = numbers.random()
        val randomSpecialCharacter = specialCharacters.random()

        return "$randomAdjective$randomNoun$randomNumber$randomSpecialCharacter"
    }

    private fun makeUserData(s: String, name: String, email: String): CreateUser {

        return CreateUser(
            authId = s,
            name = name,
            phoneNumber = "",
            email = email
        )
    }

}