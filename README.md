# Expense Tracker App 💰

A modern, feature-rich Android application built with **Kotlin** and **Jetpack Compose** to help users track their income, expenses, and budgets efficiently. This app follows Clean Architecture principles and uses the latest Android development standards.

## 📱 Screenshots

| Home Screen | Add Expense | Add Income | Search & Filter |
|:-----------:|:-----------:|:----------:|:---------------:|
| <img src="https://github.com/user-attachments/assets/58f9e651-8b7f-4c90-aa07-d31349829492" width="200" /> | <img src="https://github.com/user-attachments/assets/74be8f84-4c0d-4fd4-9474-cff1fba76b43" width="200" /> | <img src="https://github.com/user-attachments/assets/e3518e11-1b7a-467c-be77-5aa1ff930f7c" width="200" /> | <img src="https://github.com/user-attachments/assets/5734e4a3-f497-4ee7-bbbc-ccb2212fac09" width="200" /> |
| **Analytics** | **Budget** | **Transaction Detail** | **Settings** |
| <img src="https://github.com/user-attachments/assets/87ec98da-d2e5-4fd6-92d1-3eec10896ef0" width="200" /> | <img src="https://github.com/user-attachments/assets/774b4990-3257-485e-b119-7dd49a4c4f5d" width="200" /> | <img src="https://github.com/user-attachments/assets/ddb691c2-a636-4ef7-ba11-445d186dbcd1" width="200" /> | <img src="https://github.com/user-attachments/assets/65f5c40e-1ef7-4234-baa7-5ea1e100ddcc" width="200" /> |
| **Budget Detail** | **All Expenses** | 
| <img width="200" src="https://github.com/user-attachments/assets/930069e8-bac1-4928-b8e9-a3decef46234" /> | <img width="200" src="https://github.com/user-attachments/assets/47be748a-f675-42c6-bee9-eed6e36ab138" />


## ✨ Features

* **Dashboard**: View total balance, income, and expense summaries at a glance.
* **Transaction Management**: Add income or expenses with specific categories, dates, and notes.
* **Categories**: Pre-defined categories for better organization (Food, Travel, Bills, Edu, Fun, etc.).
* **Budget Planner**: Set monthly limits for specific categories and track your spending progress visually.
* **Analytics**: Visualize spending habits with monthly donut charts and category breakdowns.
* **Search & Filter**: Advanced filtering by type (Income/Expense), date range, category, and sorting options.
* **Customization**:
    * **Dark/Light Mode**: Seamless theme switching with system default support.
    * **Currency Support**: Dynamic currency symbol selection ($, €, £, ¥, ฿, etc.) that updates across the entire app.
* **Persistent Storage**: All data is saved locally using Room Database and DataStore.

## 🛠 Tech Stack

* **Language**: Kotlin
* **UI Toolkit**: [Jetpack Compose](https://developer.android.com/jetpack/compose) (Material3)
* **Architecture**: MVVM (Model-View-ViewModel)
* **Dependency Injection**: [Koin](https://insert-koin.io/)
* **Local Database**: [Room](https://developer.android.com/training/data-storage/room) (SQLite)
* **Preferences**: [Jetpack DataStore](https://developer.android.com/topic/libraries/architecture/datastore)
* **Async Programming**: Kotlin Coroutines & Flow
* **Navigation**: Jetpack Compose Navigation

