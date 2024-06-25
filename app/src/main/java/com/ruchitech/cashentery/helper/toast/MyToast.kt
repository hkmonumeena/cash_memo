package com.ruchitech.cashentery.helper.toast

import android.content.Context
import android.widget.Toast

class MyToast(private val context: Context) {
    fun showToast(msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }
}