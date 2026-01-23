package com.example.expensetracker.presentation.ui.add_expense

import CategoryModel
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import com.example.expensetracker.domain.repository.model.CategoryConstants
import com.example.expensetracker.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseScreen(
    onBackClick: () -> Unit,
    viewModel: AddExpenseViewModel = koinViewModel()
) {

    val currencySymbol by viewModel.currencySymbol.collectAsStateWithLifecycle()
    // State
    var amount by remember { mutableStateOf("") }
    var isExpense by remember { mutableStateOf(true) }
    var selectedCategory by remember { mutableStateOf("Food") }

    // NEW STATES for Date and Note
    var note by remember { mutableStateOf("") }
    var selectedDate by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showNoteDialog by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current

    // Logic to switch categories and colors
    val currentCategories = if (isExpense) CategoryConstants.expenseCategories else CategoryConstants.incomeCategories
    val themeColor by animateColorAsState(targetValue = if (isExpense) RedExpense else GreenIncome, label = "color")

    // Date Picker State
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = selectedDate)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        if(isExpense) "Add Expense" else "Add Income",
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, "Back", tint = MaterialTheme.colorScheme.onBackground)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // 1. TYPE TOGGLE
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(MaterialTheme.colorScheme.surface),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .padding(4.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isExpense) RedExpense.copy(alpha = 0.1f) else Color.Transparent)
                        .background(if (isExpense) RedExpense.copy(alpha = 0.1f) else Color.Transparent)
                        .clickable { isExpense = true; selectedCategory = "Food" },
                    contentAlignment = Alignment.Center
                ) {
                    Text("Expense", color = if (isExpense) RedExpense else Color.Gray, fontWeight = FontWeight.Bold)
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .padding(4.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (!isExpense) GreenIncome.copy(alpha = 0.1f) else Color.Transparent)
                        .clickable { isExpense = false; selectedCategory = "Salary" },
                    contentAlignment = Alignment.Center
                ) {
                    Text("Income", color = if (!isExpense) GreenIncome else Color.Gray, fontWeight = FontWeight.Bold)
                }
            }

            // 2. AMOUNT INPUT
            Spacer(modifier = Modifier.height(24.dp))
            Text("Enter Amount", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            TextField(
                value = amount,
                onValueChange = { if (it.matches(Regex("^\\d*\\.?\\d*$"))) amount = it },
                textStyle = MaterialTheme.typography.displayLarge.copy(fontSize = 56.sp, textAlign = TextAlign.Center, color = themeColor),
                placeholder = { Text("0", style = MaterialTheme.typography.displayLarge.copy(fontSize = 56.sp, color = themeColor.copy(alpha=0.5f), textAlign = TextAlign.Center), modifier = Modifier.fillMaxWidth()) },
                prefix = { Text(currencySymbol, style = MaterialTheme.typography.displayLarge.copy(fontSize = 56.sp, color = themeColor)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                modifier = Modifier.fillMaxWidth()
            )

            // 3. DATE & NOTE BUTTONS
            Spacer(modifier = Modifier.height(24.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                // DATE BUTTON
                Button(
                    onClick = { showDatePicker = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.weight(1f).height(56.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                ) {
                    Icon(Icons.Default.CalendarToday, contentDescription = null, tint = themeColor)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(formatDateForDisplay(selectedDate))
                }

                Spacer(modifier = Modifier.width(16.dp))

                // NOTE BUTTON
                Button(
                    onClick = { showNoteDialog = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.weight(1f).height(56.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                ) {
                    Icon(Icons.Default.Notes, contentDescription = null, tint = OrangeEntertainment)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (note.isEmpty()) "Add note" else note,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = if (note.isEmpty()) Color.Gray else MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // 4. CATEGORY GRID
            Spacer(modifier = Modifier.height(24.dp))
            Text("CATEGORY", style = MaterialTheme.typography.labelLarge, color = Color.Gray, modifier = Modifier.align(Alignment.Start))
            Spacer(modifier = Modifier.height(16.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.height(180.dp)
            ) {
                items(currentCategories) { category ->
                    CategoryItem(
                        category = category,
                        isSelected = selectedCategory == category.name,
                        selectedColor = themeColor,
                        onClick = { selectedCategory = category.name }
                    )
                }
            }

            // 5. SAVE BUTTON
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = {
                    viewModel.saveTransaction(
                        amount = amount,
                        category = selectedCategory,
                        note = note, // Pass the note
                        date = selectedDate, // Pass the date
                        type = if(isExpense) "EXPENSE" else "INCOME"
                    )
                    onBackClick()
                },
                modifier = Modifier.fillMaxWidth().height(56.dp).padding(bottom = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = themeColor)
            ) {
                Text("Save Transaction", fontSize = 18.sp)
            }
        }
    }


    // 1. Date Picker Dialog
    if (showDatePicker) {
        DatePickerDialog(
            shape = RoundedCornerShape(24.dp),
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { selectedDate = it }
                    showDatePicker = false
                }) { Text("OK", color = themeColor) }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel", color = Color.Gray) }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // 2. Note Dialog
    if (showNoteDialog) {
        AlertDialog(
            shape = RoundedCornerShape(24.dp),
            onDismissRequest = { showNoteDialog = false },
            title = { Text("Add Note") },
            text = {
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Note") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = themeColor,
                        focusedLabelColor = themeColor
                    )
                )
            },
            confirmButton = {
                Button(
                    onClick = { showNoteDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = themeColor)
                ) { Text("Done") }
            },
            dismissButton = {
                TextButton(onClick = { showNoteDialog = false }) { Text("Cancel", color = Color.Gray) }
            },
            containerColor = MaterialTheme.colorScheme.surface
        )
    }
}

// Helper: Format Date for Button
fun formatDateForDisplay(dateMillis: Long): String {
    val formatter = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault())
    val today = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault()).format(Date())
    val selected = formatter.format(Date(dateMillis))
    return if (selected == today) "Today" else selected
}

@Composable
fun CategoryItem(category: CategoryModel, isSelected: Boolean, selectedColor: Color, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(if (isSelected) selectedColor else MaterialTheme.colorScheme.surface)
                .clickable { onClick() }
                .then(if (!isSelected) Modifier.border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(16.dp)) else Modifier),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = category.icon,
                contentDescription = category.name,
                tint = if (isSelected) Color.White else Color.Gray
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = category.name,
            style = MaterialTheme.typography.bodySmall,
            color = if (isSelected) selectedColor else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}