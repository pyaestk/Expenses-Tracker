package com.example.expensetracker.presentation.ui.budget

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.expensetracker.domain.repository.model.CategoryConstants
import com.example.expensetracker.ui.theme.*
import org.koin.androidx.compose.koinViewModel
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetPlannerScreen(
    onBudgetClick: (Int) -> Unit,
    viewModel: BudgetViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        contentWindowInsets = ScaffoldDefaults.contentWindowInsets.exclude(WindowInsets.navigationBars),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Budget Planner", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {
            // 1. Create New Budget Button
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { showDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Create New Budget", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }

            // 2. Active Budgets Header
            Spacer(modifier = Modifier.height(32.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Active Budgets", style = MaterialTheme.typography.headlineMedium)
            }

            // 3. Budget List
            Spacer(modifier = Modifier.height(16.dp))
            if (uiState.budgetList.isEmpty() && !uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No budgets set. Tap + to start.", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(uiState.budgetList) { budget ->
                        BudgetCard(
                            budget = budget,
                            currencySymbol = uiState.currencySymbol,
                            onClick = { onBudgetClick(budget.id) }
                        )
                    }
                }
            }
        }
    }

    // Dialog to Add Budget
    if (showDialog) {
        AddBudgetDialog(
            currencySymbol = uiState.currencySymbol,
            onDismiss = { showDialog = false },
            onSave = { category, limit ->
                viewModel.saveBudget(category, limit)
                showDialog = false
            }
        )
    }
}

@Composable
fun BudgetCard(
    budget: BudgetUiModel,
    currencySymbol: String,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth().clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Header: Icon + Name + Percentage Badge
            Row(verticalAlignment = Alignment.Top) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(budget.iconBgColor, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(budget.icon, contentDescription = null, tint = budget.iconColor, modifier = Modifier.size(24.dp))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(budget.categoryName, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                    Text("Monthly Limit", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
                // Percentage Badge
                Box(
                    modifier = Modifier
                        .background(budget.color.copy(alpha = 0.1f), RoundedCornerShape(20.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    val percentText = (budget.percentage * 100).toInt()
                    val displayPercent = if(percentText > 100) "! $percentText%" else "$percentText%"
                    Text(displayPercent, color = budget.color, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }

            // Spent vs Total Numbers (UPDATED FORMATTING)
            Spacer(modifier = Modifier.height(24.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = formatCurrency(budget.spentAmount, currencySymbol), // Use Helper
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "of ${formatCurrency(budget.totalLimit, currencySymbol)}", // Use Helper
                    color = Color.Gray,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }

            // Progress Bar
            Spacer(modifier = Modifier.height(12.dp))
            LinearProgressIndicator(
                progress = { budget.percentage.coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxWidth().height(8.dp),
                color = budget.color,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                strokeCap = StrokeCap.Round,
            )

            // "Left to spend" or "Over budget" (UPDATED FORMATTING)
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                if (budget.leftAmount < 0) {
                    Text(
                        text = "Over budget by ${formatCurrency(Math.abs(budget.leftAmount), currencySymbol)}",
                        color = RedExpense,
                        fontSize = 12.sp
                    )
                } else {
                    Text(
                        text = "Left to spend: ${formatCurrency(budget.leftAmount, currencySymbol)}",
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
fun AddBudgetDialog(
    currencySymbol: String,
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit
) {
    var amount by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(CategoryConstants.expenseCategories.first().name) }
    var expanded by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text("New Budget", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)

                Spacer(modifier = Modifier.height(16.dp))

                // Category Dropdown
                Text("Category", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expanded = true }
                        .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    Text(selectedCategory)
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        CategoryConstants.expenseCategories.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category.name) },
                                onClick = {
                                    selectedCategory = category.name
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Amount Input (UPDATED LABEL)
                Text("Limit Amount ($currencySymbol)", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Actions
                Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                    TextButton(onClick = onDismiss) { Text("Cancel", color = Color.Gray) }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { onSave(selectedCategory, amount) },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }
}

// Reusable Helper
fun formatCurrency(amount: Double, symbol: String): String {
    val format = NumberFormat.getNumberInstance(Locale.US)
    format.minimumFractionDigits = 2
    format.maximumFractionDigits = 2
    return "$symbol${format.format(amount)}"
}