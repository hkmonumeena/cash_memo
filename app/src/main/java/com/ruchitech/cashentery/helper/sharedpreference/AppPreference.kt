package com.ruchitech.cashentery.helper.sharedpreference

import android.content.Context

class AppPreference(context: Context) : PreferenceConfig(context) {

    companion object {
        private const val USER_ID = "userId"
        private const val IS_LOGIN = "isUserLoggedIn"
        private const val PASSWORD = "password"
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


}

