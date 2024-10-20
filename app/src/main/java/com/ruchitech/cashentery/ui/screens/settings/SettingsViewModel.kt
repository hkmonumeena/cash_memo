package com.ruchitech.cashentery.ui.screens.settings

import androidx.compose.runtime.mutableStateOf
import com.ruchitech.cashentery.helper.SharedViewModel
import com.ruchitech.cashentery.helper.sharedpreference.AppPreference
import com.ruchitech.cashentery.retrofit.repository.AccountRepository
import com.ruchitech.cashentery.ui.screens.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val appPreference: AppPreference,
    private val repository: Repository,
    private val accountRepository: AccountRepository,
) : SharedViewModel() {

    var default = mutableStateOf(appPreference.currentMonthOnly)

    init {

    }

    fun updateSettings(newValue: Boolean = true) {
        appPreference.currentMonthOnly = newValue
    }


}