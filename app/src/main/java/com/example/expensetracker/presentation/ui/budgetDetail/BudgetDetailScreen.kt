package com.example.expensetracker.presentation.ui.budgetDetail

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.expensetracker.presentation.ui.budget.formatCurrency
import org.koin.androidx.compose.koinViewModel
import java.text.DecimalFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetDetailScreen(
    onBackClick: () -> Unit,
    viewModel: BudgetDetailViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showEditDialog by remember { mutableStateOf(false) }

    // Auto-close if deleted
    if (!uiState.isLoading && uiState.budget == null) {
        onBackClick()
        return
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Budget Detail", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) { Icon(Icons.Default.ArrowBack, "Back") }
                },
                actions = {
                    // EDIT BUTTON
                    IconButton(onClick = { showEditDialog = true }) {
                        Icon(Icons.Default.Edit, "Edit", tint = MaterialTheme.colorScheme.primary)
                    }
                    // DELETE BUTTON
                    IconButton(onClick = {
                        viewModel.deleteBudget()
                        onBackClick()
                    }) {
                        Icon(Icons.Default.Delete, "Delete", tint = MaterialTheme.colorScheme.error)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        } else {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // 1. Title Section
                Text(
                    text = uiState.budget?.category ?: "Category",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Budget Analysis",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(40.dp))

                // 2. Analytics-style Donut Chart
                Box(contentAlignment = Alignment.Center) {
                    BudgetDonutChart(
                        spent = uiState.spentAmount.toFloat(),
                        limit = uiState.totalLimit.toFloat(),
                        color = uiState.statusColor,
                        modifier = Modifier.size(220.dp)
                    )

                    // Text inside Chart
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        val percent = (uiState.percentage * 100).toInt()
                        Text(
                            text = "$percent%",
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.Bold,
                            color = uiState.statusColor
                        )
                        Text(
                            text = if(percent > 100) "Over Budget" else "Used",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(48.dp))

                // 3. Stats Grid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    BudgetStatItem(
                        label = "Limit",
                        amount = uiState.totalLimit,
                        symbol = uiState.currencySymbol,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    BudgetStatItem(
                        label = "Spent",
                        amount = uiState.spentAmount,
                        symbol = uiState.currencySymbol,
                        color = uiState.statusColor
                    )
                    BudgetStatItem(
                        label = "Remaining",
                        amount = uiState.totalLimit - uiState.spentAmount,
                        symbol = uiState.currencySymbol,
                        color = if (uiState.totalLimit - uiState.spentAmount < 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }

    // 4. Specific Edit Dialog
    if (showEditDialog && uiState.budget != null) {
        EditBudgetDialog(
            currencySymbol = uiState.currencySymbol,
            categoryName = uiState.budget!!.category,
            currentLimit = uiState.budget!!.amountLimit,
            onDismiss = { showEditDialog = false },
            onSave = { newAmount ->
                viewModel.updateBudget(newAmount)
                showEditDialog = false
            }
        )
    }
}

// --- NEW COMPONENT: Budget Donut Chart ---
@Composable
fun BudgetDonutChart(
    spent: Float,
    limit: Float,
    color: Color,
    modifier: Modifier = Modifier,
    thickness: Dp = 24.dp
) {
    // If spent > limit, fill the whole circle. Otherwise calculate sweep.
    val progress = (spent / limit).coerceIn(0f, 1f)
    val sweepAngle = progress * 360f
    val remainingColor = MaterialTheme.colorScheme.surfaceVariant

    Canvas(modifier = modifier) {
        val stroke = Stroke(width = thickness.toPx(), cap = StrokeCap.Round)
        val size = Size(size.width, size.height)

        // 1. Draw Background Circle (Remaining)
        drawArc(
            color = remainingColor,
            startAngle = 0f,
            sweepAngle = 360f,
            useCenter = false,
            style = stroke,
            size = size
        )

        // 2. Draw Progress Arc (Spent)
        drawArc(
            color = color,
            startAngle = -90f, // Start from top
            sweepAngle = sweepAngle,
            useCenter = false,
            style = stroke,
            size = size
        )
    }
}

// --- NEW COMPONENT: Stat Item ---
@Composable
fun BudgetStatItem(
    label: String,
    amount: Double,
    symbol: String,
    color: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = formatCurrency(Math.abs(amount), symbol),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

// --- NEW COMPONENT: Edit Dialog ---
@Composable
fun EditBudgetDialog(
    currencySymbol: String,
    categoryName: String,
    currentLimit: Double,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    // Pre-fill amount (remove decimal if .0)
    val decimalFormat = DecimalFormat("#.##")
    var amount by remember { mutableStateOf(decimalFormat.format(currentLimit)) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = "Edit Budget",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(24.dp))

                // 1. Read-only Category
                Text("Category", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = categoryName,
                    onValueChange = {},
                    enabled = false, // Locked
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                        disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 2. Editable Amount
                Text("Limit Amount ($currencySymbol)", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = amount,
                    onValueChange = { if (it.matches(Regex("^\\d*\\.?\\d*$"))) amount = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(32.dp))

                // 3. Actions
                Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel", color = Color.Gray)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Button(
                        onClick = { onSave(amount) },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Update Limit")
                    }
                }
            }
        }
    }
}