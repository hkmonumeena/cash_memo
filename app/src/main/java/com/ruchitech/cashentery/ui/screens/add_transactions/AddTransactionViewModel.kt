package com.ruchitech.cashentery.ui.screens.add_transactions

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.database.FirebaseDatabase
import com.ruchitech.cashentery.helper.Result
import com.ruchitech.cashentery.helper.sharedpreference.AppPreference
import com.ruchitech.cashentery.helper.toast.MyToast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

@HiltViewModel
class AddTransactionViewModel @Inject constructor(
    private val myToast: MyToast,
    private val appPreference: AppPreference,
) : ViewModel() {
    val showLoading = mutableStateOf(false)
    private val _result = MutableStateFlow<Result?>(null)
    val result: StateFlow<Result?> = _result

    init {
        Log.e("kiihgfh", "${appPreference.userId}")
    }

    fun addTrans(addNewTransaction: AddTransactionData) {
        /*        val mainCategory = MainCategory(id = 1L, name = "Income")
                val subCategory = SubCategory(id = 1L, name = "Salary")

                val transaction = AddTransaction(
                    date = "2023-06-23",
                    type = Type.CREDIT,
                    account = Account.ONLINE,
                    transactionNumber = "TXN123456",
                    amount = 1000.0,
                    remarks = "Monthly salary",
                    mainCategory = mainCategory,
                    subCategory = subCategory
                )*/

        storeTransaction(addNewTransaction)
    }


    private fun storeTransaction(transaction: AddTransactionData) {
        showLoading.value = true
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("transactions").child(appPreference.userId ?: "")
        val transactionId = myRef.push().key // Generate a unique ID for the transaction

        if (transactionId != null) {
            val transactionWithId = transaction.copy(id = transactionId)
            val jsonString = Json.encodeToString(transactionWithId)

            myRef.child(transactionId).setValue(jsonString)
                .addOnSuccessListener {
                    showLoading.value = false
                    myToast.showToast("Transaction successfully stored.")
                    _result.value = Result.Success
                    println("Transaction successfully stored.")
                }
                .addOnFailureListener { e ->
                    showLoading.value = false
                    myToast.showToast("Failed to store transaction: ${e.message}")
                    println("Failed to store transaction: ${e.message}")
                    _result.value = Result.Success
                }
        } else {
            showLoading.value = false
            _result.value = Result.Success
            println("Failed to generate transaction ID.")
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