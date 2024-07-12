package com.ruchitech.cashentery.ui.screens.chatview

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ruchitech.cashentery.helper.Result
import com.ruchitech.cashentery.helper.sharedpreference.AppPreference
import com.ruchitech.cashentery.helper.toast.MyToast
import com.ruchitech.cashentery.ui.screens.add_transactions.AddTransactionData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

@HiltViewModel
class TransactionDetailsViewModel @Inject constructor(private val appPreference: AppPreference,private val myToast: MyToast) : ViewModel() {
    private val _transactionsFlow = MutableStateFlow<List<AddTransactionData>>(emptyList())
    val transactionsFlow: StateFlow<List<AddTransactionData>> = _transactionsFlow
    private val _categories = MutableStateFlow(if (appPreference.categoriesList.isNullOrEmpty()) arrayListOf() else appPreference.categoriesList)
    val categories: StateFlow<ArrayList<String?>?> = _categories
    val showLoading = mutableStateOf(false)
    private val db = FirebaseFirestore.getInstance()

    private val _result = MutableStateFlow<Result?>(null)
    val result: StateFlow<Result?> = _result

    fun getTransactionDetails(transactionId: String) {
        // Define the type for a list of AddTransactionData
        val listType = object : TypeToken<List<AddTransactionData>>() {}.type

// Parse the JSON into a list of AddTransactionData objects
        val transactions: List<AddTransactionData> = Gson().fromJson(transactionId, listType)
        _transactionsFlow.value = transactions.sortedBy { it.timeInMiles }
    }

    fun updateTransaction(updatedTransaction: AddTransactionData) {
        val currentTransactions = _transactionsFlow.value
        val updatedTransactions = currentTransactions.map { transaction ->
            if (transaction.id == updatedTransaction.id) {
                updatedTransaction
            } else {
                transaction
            }
        }
        _transactionsFlow.value = updatedTransactions
        updateTransactionDb(updatedTransaction)
    }

    private fun updateTransactionDb(transaction: AddTransactionData) {
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
            val transactionWithId = transaction.id
            val jsonString = Json.encodeToString(transactionWithId)
            Log.e("gfjknuigfg", "storeTransaction: $jsonString")
            db.collection("users").document(appPreference.userId?:"").collection("transactions")
                .document(transaction.id?:"") // assume transactionData has an id property
                .set(transactionData, SetOptions.merge())
                .addOnSuccessListener {
                    Log.d("MyViewModel", "Transaction updated successfully")
                    showLoading.value = false
                    myToast.showToast("Transaction updated successfully.")
                    _result.value = Result.Success
                    println("Transaction updated successfully.")
                }
                .addOnFailureListener { exception ->
                    Log.e("MyViewModel", "Error updating transaction: ${exception.message}")
                    showLoading.value = false
                    myToast.showToast("Failed to update transaction: ${exception.message}")
                    println("Failed to update transaction: ${exception.message}")
                    _result.value = Result.Success
                }

        } else {
            showLoading.value = false
            _result.value = Result.Error
            println("Failed to generate transaction ID.")
        }
    }

     fun deleteTransactionDb(transactionId: String, dataToEdit: Int) {
        showLoading.value = true
        db.collection("users").document(appPreference.userId?:"").collection("transactions")
            .document(transactionId)
            .delete()
            .addOnSuccessListener {
                Log.d("MyViewModel", "Transaction deleted successfully")
                val currentTransactions = _transactionsFlow.value.toMutableList()
                val dataToDelete = currentTransactions.find { it.id == transactionId }
                currentTransactions.removeAt(dataToEdit)
                _transactionsFlow.value = currentTransactions
                if (currentTransactions.isEmpty()){
                    _result.value = Result.Success
                }
                showLoading.value = false
                myToast.showToast("Transaction deleted successfully.")
                _result.value = Result.Success
                println("Transaction deleted successfully.")
            }
            .addOnFailureListener { exception ->
                Log.e("MyViewModel", "Error deleting transaction: ${exception.message}")
                showLoading.value = false
                myToast.showToast("Failed to delete transaction: ${exception.message}")
                println("Failed to delete transaction: ${exception.message}")
                _result.value = Result.Error
            }
    }
}