package com.ruchitech.cashentery.helper.sharedpreference

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ruchitech.cashentery.ui.screens.mobile_auth.data.CreateUser

class AppPreference(context: Context) : PreferenceConfig(context) {

    companion object {
        private const val USER_ID = "userId"
        private const val IS_LOGIN = "isUserLoggedIn"
        private const val MOBILE = "mobile_number"
        private const val PASSWORD = "password"
        private const val CATEGORIES = "CATEGOROIES"
        private const val USER_DATA = "USER_DATA"
        private const val CURRENT_MONTH_ONLY_TRNX = "CURRENT_MONTH_ONLY_TRNX"
    }

    var isUserLoggedIn: Boolean
        get() = getPreference(IS_LOGIN, PrefConfig.BooleanValue())
        set(value) = setPreference(IS_LOGIN, PrefConfig.BooleanValue(value))

    var currentMonthOnly: Boolean
        get() = getPreference(CURRENT_MONTH_ONLY_TRNX, PrefConfig.BooleanValue())
        set(value) = setPreference(CURRENT_MONTH_ONLY_TRNX, PrefConfig.BooleanValue(value))

    var userId: String?
        get() = getPreference(USER_ID, PrefConfig.StringValue())
        set(value) = setPreference(USER_ID, PrefConfig.StringValue(value))

    var mobileNumber: String?
        get() = getPreference(MOBILE, PrefConfig.StringValue())
        set(value) = setPreference(MOBILE, PrefConfig.StringValue(value))

    var password: String?
        get() = getPreference(PASSWORD, PrefConfig.StringValue())
        set(value) = setPreference(PASSWORD, PrefConfig.StringValue(value))

    var userData: CreateUser?
        get() = Gson().fromJson(
            getPreference(USER_DATA, PrefConfig.StringValue()) as String,
            CreateUser::class.java
        )
        set(value) = setPreference(USER_DATA, PrefConfig.StringValue(Gson().toJson(value)))

    var categoriesList: List<String>
        get() {
            val jsonString = getPreference(CATEGORIES, PrefConfig.StringValue()) as String?
            val listType = object : TypeToken<List<String>>() {}.type
            val list: List<String> = Gson().fromJson(jsonString, listType) ?: emptyList()
            return list.distinct()
        }
        set(value) {
            val uniqueList = value.distinct() // Remove duplicates before setting
            setPreference(CATEGORIES, PrefConfig.StringValue(Gson().toJson(uniqueList)))
        }


}

