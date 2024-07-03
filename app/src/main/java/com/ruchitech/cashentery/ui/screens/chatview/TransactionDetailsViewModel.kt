package com.ruchitech.cashentery.ui.screens.chatview

import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ruchitech.cashentery.ui.screens.add_transactions.AddTransactionData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class TransactionDetailsViewModel @Inject constructor() : ViewModel() {
    private val _transactionsFlow = MutableStateFlow<List<AddTransactionData>>(emptyList())
    val transactionsFlow: StateFlow<List<AddTransactionData>> = _transactionsFlow
    fun getTransactionDetails(transactionId: String) {
        // Define the type for a list of AddTransactionData
        val listType = object : TypeToken<List<AddTransactionData>>() {}.type

// Parse the JSON into a list of AddTransactionData objects
        val transactions: List<AddTransactionData> = Gson().fromJson(transactionId, listType)
        _transactionsFlow.value = transactions.sortedBy { it.timeInMiles }
    }
}