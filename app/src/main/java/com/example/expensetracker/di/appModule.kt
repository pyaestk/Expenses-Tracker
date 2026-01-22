package com.example.expensetracker.di

import androidx.room.Room
import com.example.expensetracker.data.local.AppDatabase
import com.example.expensetracker.data.repository.ExpenseRepository
import com.example.expensetracker.data.repository.ExpenseRepositoryImpl
import com.example.expensetracker.data.repository.UserPreferencesRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val appModule = module {


    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "expense_tracker_db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    single { get<AppDatabase>().transactionDao() }
    single { get<AppDatabase>().budgetDao() }


    single<ExpenseRepository> {
        ExpenseRepositoryImpl(
            transactionDao = get(),
            budgetDao = get()
        )
    }

    single {
        UserPreferencesRepository(androidContext())
    }
}