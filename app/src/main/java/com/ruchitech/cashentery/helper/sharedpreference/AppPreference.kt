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

    var categoriesList: ArrayList<String?>?
        get() = Gson().fromJson(
            getPreference(CATEGORIES, PrefConfig.StringValue()) as String?,
            object : TypeToken<List<String?>>() {}.type
        )
        set(value) = setPreference(CATEGORIES, PrefConfig.StringValue(Gson().toJson(value)))


}

