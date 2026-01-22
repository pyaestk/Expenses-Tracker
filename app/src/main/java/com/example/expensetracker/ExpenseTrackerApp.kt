package com.example.expensetracker

import android.app.Application
import com.example.expensetracker.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import viewModelModule

class ExpenseTrackerApp: Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@ExpenseTrackerApp)
            modules(
                appModule,
                viewModelModule

            )
        }
    }
}