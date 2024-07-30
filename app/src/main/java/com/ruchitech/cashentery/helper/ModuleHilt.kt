package com.ruchitech.cashentery.helper

import android.content.Context
import com.google.firebase.firestore.FirebaseFirestore
import com.ruchitech.cashentery.helper.sharedpreference.AppPreference
import com.ruchitech.cashentery.helper.toast.MyToast
import com.ruchitech.cashentery.ui.screens.Repository
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


    @Provides
    @Singleton
    fun provideFirestoreInstance(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Singleton
    @Provides
    fun provideRepository(appPreference: AppPreference, firestore: FirebaseFirestore): Repository {
        return Repository(appPreference,firestore)
    }

    @Singleton
    @Provides
    fun context(@ApplicationContext context: Context): Context = context

}