package com.ruchitech.cashentery.ui.screens.add_transactions

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.ruchitech.cashentery.helper.Event
import com.ruchitech.cashentery.helper.EventEmitter
import com.ruchitech.cashentery.helper.Result
import com.ruchitech.cashentery.helper.SharedViewModel
import com.ruchitech.cashentery.helper.sharedpreference.AppPreference
import com.ruchitech.cashentery.helper.toast.MyToast
import com.ruchitech.cashentery.retrofit.remote.Status
import com.ruchitech.cashentery.retrofit.repository.AccountRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class AddTransactionViewModel @Inject constructor(
    private val myToast: MyToast,
    private val appPreference: AppPreference,
    private val accountRepository: AccountRepository
) : SharedViewModel() {
    val showLoading = mutableStateOf(false)
    private val _result = MutableStateFlow<Result?>(null)
    val result: StateFlow<Result?> = _result
    private val db = FirebaseFirestore.getInstance()
    private val _categories =
        MutableStateFlow(appPreference.categoriesList.ifEmpty { arrayListOf() })
     val categories: StateFlow<List<String>> = _categories

    init {
        Log.e("kiihgfh", "${appPreference.userId}")
    }

    fun addTrans(addNewTransaction: Transaction) {
        val tempTagList = categories.value.toMutableList()
        tempTagList.add(
            addNewTransaction.tag?.trim()
                ?.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }?:"")
        _categories.value = tempTagList.toList()
        appPreference.categoriesList = categories.value
        storeTransaction(addNewTransaction)
    }


    private fun storeTransaction(transaction: Transaction) {
        viewModelScope.launch(Dispatchers.IO) {
            val transactionData = hashMapOf(
                "account" to transaction.account,
                "amount" to transaction.amount,
                "date" to transaction.date,
                "remarks" to transaction.remarks,
                "tag" to transaction.tag,
                "timeInMiles" to transaction.timeInMiles,
                "transactionNumber" to transaction.transactionNumber,
                "type" to transaction.type,
                "status" to transaction.status,
                "authId" to appPreference.userId
            )



            viewModelScope.launch {
                var newTrnx = transaction.copy(authId = appPreference.userId)
                accountRepository.createTransaction(newTrnx).distinctUntilChanged()
                    .collectLatest { resources ->
                        when (resources.status) {
                            Status.INITIAL -> Unit
                            Status.EMPTY -> Unit
                            Status.SUCCESS -> {
                                CoroutineScope(Dispatchers.IO).launch {
                                    EventEmitter postEvent Event.TransactionDetailsViewModel(
                                        transaction = transaction
                                    )
                                    delay(1200)
                                    EventEmitter postEvent Event.HomeViewModel(
                                        refreshPage = true
                                    )
                                    delay(1400)
                                    EventEmitter postEvent Event.TransactionsViewModel(refreshPage = true)
                                    delay(2000)
                                    showLoading.value = false
                                    _result.value = Result.Success
                                }
                                println("Transaction successfully stored.")
                            }

                            Status.ERROR -> {
                                showLoading.value = false
                            }

                            Status.LOADING -> {
                                showLoading.value = true
                            }
                        }
                    }
            }

            Log.e("lkfgjiihdgfdg", "$transaction")
            /*val database = FirebaseDatabase.getInstance()
            val myRef = database.getReference("transactions").child(appPreference.userId ?: "")
            val transactionId = myRef.push().key // Generate a unique ID for the transaction

            if (transactionId != null) {
                val transactionWithId = transaction.copy(id = transactionId)
                val jsonString = Json.encodeToString(transactionWithId)

                db.collection("users").document(appPreference.userId ?: "")
                    .collection("transactions")
                    .add(transactionData)
                    .addOnSuccessListener { documentReference ->
                        Log.d("MyViewModel", "Transaction added with ID: ${documentReference.id}")
                        CoroutineScope(Dispatchers.IO).launch {
                            EventEmitter postEvent Event.TransactionDetailsViewModel(
                                transaction = transaction.copy(id = documentReference.id)
                            )
                            delay(1200)
                            EventEmitter postEvent Event.HomeViewModel(
                                refreshPage = true
                            )
                            delay(1400)
                            EventEmitter postEvent Event.TransactionsViewModel(refreshPage = true)
                            delay(2000)
                            showLoading.value = false
                            _result.value = Result.Success
                        }
                        println("Transaction successfully stored.")
                    }
                    .addOnFailureListener { exception ->
                        Log.e("MyViewModel", "Error adding transaction: ${exception.message}")
                        showLoading.value = false
                        myToast.showToast("Failed to store transaction: ${exception.message}")
                        println("Failed to store transaction: ${exception.message}")
                        _result.value = Result.Success
                    }
            } else {
                CoroutineScope(Dispatchers.IO).launch {
                    delay(2000)
                    showLoading.value = false
                }
                _result.value = Result.Success
            }
        }*/
        }
    }

    private suspend fun getAllTransactions(): List<Transaction> {
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("transactions")

        return try {
            val snapshot = myRef.get().await()
            val transactions = mutableListOf<Transaction>()

            for (childSnapshot in snapshot.children) {
                val jsonString = childSnapshot.getValue(String::class.java)
                if (jsonString != null) {
                    val transaction = Json.decodeFromString<Transaction>(jsonString)
                    transactions.add(transaction)
                }
            }
            transactions
        } catch (e: Exception) {
            println("Error retrieving transactions: ${e.message}")
            emptyList()
        }
    }

}