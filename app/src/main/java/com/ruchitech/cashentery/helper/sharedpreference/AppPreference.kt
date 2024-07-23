package com.ruchitech.cashentery.helper.sharedpreference

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class AppPreference(context: Context) : PreferenceConfig(context) {

    companion object {
        private const val USER_ID = "userId"
        private const val IS_LOGIN = "isUserLoggedIn"
        private const val PASSWORD = "password"
        private const val CATEGORIES = "CATEGOROIES"
    }

    var isUserLoggedIn: Boolean
        get() = getPreference(IS_LOGIN, PrefConfig.BooleanValue())
        set(value) = setPreference(IS_LOGIN, PrefConfig.BooleanValue(value))

    var userId: String?
        get() = getPreference(USER_ID, PrefConfig.StringValue())
        set(value) = setPreference(USER_ID, PrefConfig.StringValue(value))

    var password: String?
        get() = getPreference(PASSWORD, PrefConfig.StringValue())
        set(value) = setPreference(PASSWORD, PrefConfig.StringValue(value))

    var categoriesList: List<String>
        get() {
            val jsonString = getPreference(CATEGORIES, PrefConfig.StringValue()) as String?
            val listType = object : TypeToken<List<String>>() {}.type
            val list: List<String> = Gson().fromJson(jsonString, listType) ?: emptyList()
            return list.distinct() // Ensure uniqueness in the getter
        }
        set(value) {
            val uniqueList = value.distinct() // Remove duplicates before setting
            setPreference(CATEGORIES, PrefConfig.StringValue(Gson().toJson(uniqueList)))
        }



}

