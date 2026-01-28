package com.example.saveit.presentation.ui.analytics

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.saveit.ui.theme.BluePrimary
import org.koin.androidx.compose.koinViewModel
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(
    onCategoryClick: (String, Long, Long) -> Unit,
    viewModel: AnalyticsViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        contentWindowInsets = ScaffoldDefaults.contentWindowInsets.exclude(WindowInsets.navigationBars),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Analytics", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {

            // 1. Month Selector
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.changeMonth(-1) }) {
                    Icon(Icons.Default.ChevronLeft, contentDescription = "Previous")
                }
                Box(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(20.dp))
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(uiState.dateDisplay, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                IconButton(onClick = { viewModel.changeMonth(1) }) {
                    Icon(Icons.Default.ChevronRight, contentDescription = "Next")
                }
            }

            // 2. Donut Chart with Total in Center
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp)
            ) {
                DonutChart(
                    data = uiState.categories,
                    modifier = Modifier.size(200.dp)
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Total Spending", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                    // UPDATE: Use dynamic currency formatter
                    Text(
                        text = formatCurrency(uiState.totalSpending, uiState.currencySymbol),
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        color = BluePrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 3. Category List
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(uiState.categories) { category ->
                    CategoryAnalyticsItem(
                        category = category,
                        currencySymbol = uiState.currencySymbol,
                        onClick = {
                            onCategoryClick(category.name, uiState.startDate, uiState.endDate)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryAnalyticsItem(
    category: CategoryAnalyticsModel,
    currencySymbol: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(category.color.copy(alpha = 0.1f), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(category.icon, contentDescription = null, tint = category.color)
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Name & Percentage
        Column(modifier = Modifier.weight(1f)) {
            Text(category.name, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(4.dp))
            LinearProgressIndicator(
                progress = { category.percentage },
                modifier = Modifier.fillMaxWidth().height(6.dp),
                color = category.color,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                strokeCap = StrokeCap.Round
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Amount - UPDATE: Use dynamic currency formatter
        Text(
            text = formatCurrency(category.amount, currencySymbol),
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
    }
}

@Composable
fun DonutChart(
    data: List<CategoryAnalyticsModel>,
    modifier: Modifier = Modifier,
    thickness: Dp = 30.dp
) {
    val total = data.sumOf { it.amount.toDouble() }.toFloat()

    Canvas(modifier = modifier) {
        var startAngle = -90f // Start from top
        val stroke = Stroke(width = thickness.toPx(), cap = StrokeCap.Round)

        data.forEach { item ->
            val sweepAngle = (item.amount.toFloat() / total) * 360f

            drawArc(
                color = item.color,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = stroke,
                size = Size(size.width, size.height)
            )

            startAngle += sweepAngle
        }
    }
}

// Helper Function for formatting
fun formatCurrency(amount: Double, symbol: String): String {
    val format = NumberFormat.getNumberInstance(Locale.US)
    format.minimumFractionDigits = 2
    format.maximumFractionDigits = 2
    return "$symbol${format.format(amount)}"
}