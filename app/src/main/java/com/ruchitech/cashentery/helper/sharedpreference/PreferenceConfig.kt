package com.ruchitech.cashentery.helper.sharedpreference

import android.content.Context
import android.content.SharedPreferences

abstract class PreferenceConfig(private val context: Context) {
    private val preferenceFile = "your_preference_file"
    protected val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(preferenceFile, Context.MODE_PRIVATE)


    fun clearData() {
        context.getSharedPreferences(preferenceFile, Context.MODE_PRIVATE).edit().clear().apply()
    }

    sealed class PrefConfig {
        data class StringValue(val value: String? = "") : PrefConfig()
        data class IntValue(val value: Int? = 0) : PrefConfig()
        data class LongValue(val value: Long? = 0) : PrefConfig()
        data class FloatValue(val value: Float? = 0f) : PrefConfig()
        data class BooleanValue(val value: Boolean? = false) : PrefConfig()
    }

    protected inline fun <reified T> getPreference(key: String, defaultValue: PrefConfig): T {
        return when (defaultValue) {
            is PrefConfig.LongValue -> {
                defaultValue.value?.let { sharedPreferences.getLong(key, it) } as T
            }

            is PrefConfig.BooleanValue -> defaultValue.value?.let {
                sharedPreferences.getBoolean(
                    key,
                    it
                )
            } as T

            is PrefConfig.FloatValue -> defaultValue.value?.let {
                sharedPreferences.getFloat(
                    key,
                    it
                )
            } as T

            is PrefConfig.IntValue -> defaultValue.value?.let {
                sharedPreferences.getInt(
                    key,
                    it
                )
            } as T

            is PrefConfig.StringValue -> sharedPreferences.getString(
                key,
                defaultValue.value
            ) as T
        }
    }

    protected fun setPreference(key: String, value: PrefConfig) {
        sharedPreferences.edit().apply {
            when (value) {
                is PrefConfig.LongValue -> value.value?.let { putLong(key, it) }
                is PrefConfig.BooleanValue -> value.value?.let { putBoolean(key, it) }
                is PrefConfig.FloatValue -> value.value?.let { putFloat(key, it) }
                is PrefConfig.IntValue -> value.value?.let { putInt(key, it) }
                is PrefConfig.StringValue -> putString(key, value.value)
            }
            apply()
        }
    }
}