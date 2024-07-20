package com.ruchitech.cashentery.ui.screens.add_transactions

import android.os.Handler
import android.os.Looper
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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
) : SharedViewModel() {
    val showLoading = mutableStateOf(false)
    private val _result = MutableStateFlow<Result?>(null)
    val result: StateFlow<Result?> = _result
    private val db = FirebaseFirestore.getInstance()
    private val _categories =
        MutableStateFlow(if (appPreference.categoriesList.isNullOrEmpty()) arrayListOf() else appPreference.categoriesList)
    val categories: StateFlow<ArrayList<String?>?> = _categories

    init {
        Log.e("kiihgfh", "${appPreference.userId}")
    }

    fun addTrans(addNewTransaction: AddTransactionData) {
        categories.value?.add(
            addNewTransaction.tag?.trim()
                ?.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() })
        appPreference.categoriesList = categories.value
        storeTransaction(addNewTransaction)
    }


    private fun storeTransaction(transaction: AddTransactionData) {
        viewModelScope.launch(Dispatchers.IO) {
            val transactionData = hashMapOf(
                "account" to transaction.account,
                "amount" to transaction.amount,
                "date" to transaction.date,
                "remarks" to transaction.remarks,
                "tag" to transaction.tag,
                "timeInMiles" to transaction.timeInMiles,
                "transactionNumber" to transaction.transactionNumber,
                "type" to transaction.type
            )
            showLoading.value = true
            val database = FirebaseDatabase.getInstance()
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
                        EventEmitter postEvent Event.HomeViewModel(
                            refreshPage = true
                        )
                        Handler(Looper.getMainLooper()).postDelayed({
                            showLoading.value = false
                            EventEmitter postEvent Event.TransactionDetailsViewModel(
                                addTransactionData = transaction.copy(id = documentReference.id)
                            )
                            myToast.showToast("Transaction successfully stored.")
                            _result.value = Result.Success
                            EventEmitter postEvent Event.TransactionsViewModel(refreshPage = true)
        /*                    Handler(Looper.getMainLooper()).postDelayed({

                            }, 1200)*/

                        }, 1200)



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
                showLoading.value = false
                _result.value = Result.Success
                println("Failed to generate transaction ID.")
            }
        }
    }

    private suspend fun getAllTransactions(): List<AddTransactionData> {
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("transactions")

        return try {
            val snapshot = myRef.get().await()
            val transactions = mutableListOf<AddTransactionData>()

            for (childSnapshot in snapshot.children) {
                val jsonString = childSnapshot.getValue(String::class.java)
                if (jsonString != null) {
                    val transaction = Json.decodeFromString<AddTransactionData>(jsonString)
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