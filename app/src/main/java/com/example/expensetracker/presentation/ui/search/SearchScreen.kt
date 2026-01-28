package com.example.expensetracker.presentation.ui.search

import TransactionItem
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.expensetracker.domain.repository.model.CategoryConstants
import org.koin.androidx.compose.koinViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onBackClick: () -> Unit,
    onTransactionClick: (Int) -> Unit,
    viewModel: SearchViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showCategorySheet by remember { mutableStateOf(false) }

    Scaffold(
        // Ensure we respect window insets (prevent overlap with status bar)
        contentWindowInsets = ScaffoldDefaults.contentWindowInsets,
        topBar = {
            // Custom Search Bar Container
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background)
                    .statusBarsPadding() // Handled status bar padding
                    .padding(top = 8.dp, bottom = 8.dp)
            ) {
                SearchBar(
                    query = uiState.query,
                    onQueryChange = viewModel::onQueryChange,
                    onBackClick = onBackClick
                )
                // Filter Chips Row
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    // 1. Type Filter
                    item {
                        FilterDropdown(
                            label = "Type: ${uiState.typeFilter.name.lowercase().capitalize()}",
                            options = TransactionTypeFilter.values().toList(),
                            selected = uiState.typeFilter,
                            onSelect = viewModel::onTypeChange
                        )
                    }
                    // 2. Date Filter
                    item {
                        FilterDropdown(
                            label = uiState.dateFilter.name.replace("_", " ").lowercase().capitalize(),
                            options = DateFilter.values().toList(),
                            selected = uiState.dateFilter,
                            onSelect = viewModel::onDateChange
                        )
                    }
                    // 3. Category Filter (Click to open sheet)
                    item {
                        FilterChip(
                            selected = uiState.selectedCategory != null,
                            onClick = { showCategorySheet = true },
                            label = { Text(uiState.selectedCategory ?: "Category") },
                            trailingIcon = if (uiState.selectedCategory != null) {
                                {
                                    Icon(
                                        Icons.Default.Close,
                                        contentDescription = "Clear",
                                        modifier = Modifier.size(16.dp).clickable { viewModel.onCategoryChange(null) }
                                    )
                                }
                            } else null
                        )
                    }
                    // 4. Sort
                    item {
                        FilterDropdown(
                            label = "Sort: ${uiState.sortOption.name.replace("_", " ").lowercase().capitalize()}",
                            options = SortOption.values().toList(),
                            selected = uiState.sortOption,
                            onSelect = viewModel::onSortChange
                        )
                    }
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        if (uiState.results.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("No transactions found", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding).fillMaxSize(),
                // FIX: Increased bottom padding to 48.dp to clear the screen edge/gesture bar
                contentPadding = PaddingValues(top = 16.dp, bottom = 48.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.results) { transaction ->
                    TransactionItem(
                        transaction = transaction,
                        onClick = { onTransactionClick(transaction.id) }
                    )
                }
            }
        }
    }

    // Category Selection Bottom Sheet
    if (showCategorySheet) {
        ModalBottomSheet(onDismissRequest = { showCategorySheet = false }) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Select Category", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                LazyColumn {
                    item {
                        ListItem(
                            headlineContent = { Text("All Categories") },
                            modifier = Modifier.clickable {
                                viewModel.onCategoryChange(null)
                                showCategorySheet = false
                            }
                        )
                    }
                    items((CategoryConstants.expenseCategories + CategoryConstants.incomeCategories)) { cat ->
                        ListItem(
                            headlineContent = { Text(cat.name) },
                            leadingContent = { Icon(cat.icon, null, tint = cat.color) },
                            modifier = Modifier.clickable {
                                viewModel.onCategoryChange(cat.name)
                                showCategorySheet = false
                            }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onBackClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(56.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(28.dp)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBackClick) {
            Icon(Icons.Default.ArrowBack, "Back")
        }
        TextField(
            value = query,
            onValueChange = onQueryChange,
            placeholder = { Text("Search transactions...") },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            singleLine = true,
            modifier = Modifier.weight(1f)
        )
        if (query.isNotEmpty()) {
            IconButton(onClick = { onQueryChange("") }) {
                Icon(Icons.Default.Clear, "Clear")
            }
        }
    }
}

// Generic Dropdown Chip
@Composable
fun <T> FilterDropdown(
    label: String,
    options: List<T>,
    selected: T,
    onSelect: (T) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        FilterChip(
            selected = false,
            onClick = { expanded = true },
            label = { Text(label) },
            trailingIcon = { Icon(Icons.Default.FilterList, null, modifier = Modifier.size(16.dp)) }
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = {
                        val text = option.toString().replace("_", " ").lowercase().capitalize()
                        Text(text, fontWeight = if(option == selected) FontWeight.Bold else FontWeight.Normal)
                    },
                    onClick = {
                        onSelect(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

fun String.capitalize() = replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }