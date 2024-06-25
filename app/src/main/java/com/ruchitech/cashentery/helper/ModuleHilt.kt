package com.ruchitech.cashentery.helper

import android.content.Context
import com.ruchitech.cashentery.helper.sharedpreference.AppPreference
import com.ruchitech.cashentery.helper.toast.MyToast
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ModuleHilt {

    @Singleton
    @Provides
    fun myToast(@ApplicationContext context: Context): MyToast = MyToast(context)

    @Singleton
    @Provides
    fun sharedPref(@ApplicationContext context: Context): AppPreference = AppPreference(context)

    @Singleton
    @Provides
    fun context(@ApplicationContext context: Context): Context = context

}