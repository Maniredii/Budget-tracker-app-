package fm.mrc.budget

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fm.mrc.budget.ui.theme.*
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.math.max
import kotlin.math.min

object ExpenseColors {
    val FOOD = Color(0xFFFFA726)         // Orange
    val SHOPPING = Color(0xFF66BB6A)     // Green
    val TRANSPORTATION = Color(0xFF42A5F5) // Blue
    val ENTERTAINMENT = Color(0xFFEC407A) // Pink
    val UTILITIES = Color(0xFF7E57C2)    // Purple
    val HEALTH = Color(0xFFEF5350)       // Red
    val EDUCATION = Color(0xFF26A69A)    // Teal
    val TRAVEL = Color(0xFF5C6BC0)       // Indigo
    val HOUSING = Color(0xFF8D6E63)      // Brown
    val OTHERS = Color(0xFF78909C)       // Grey

    fun getColorForCategory(category: ExpenseCategory): Color {
        return when (category) {
            ExpenseCategory.FOOD -> FOOD
            ExpenseCategory.SHOPPING -> SHOPPING
            ExpenseCategory.TRANSPORTATION -> TRANSPORTATION
            ExpenseCategory.ENTERTAINMENT -> ENTERTAINMENT
            ExpenseCategory.UTILITIES -> UTILITIES
            ExpenseCategory.HEALTH -> HEALTH
            ExpenseCategory.EDUCATION -> EDUCATION
            ExpenseCategory.TRAVEL -> TRAVEL
            ExpenseCategory.HOUSING -> HOUSING
            else -> OTHERS
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseCharts(transactions: List<Transaction>) {
    if (transactions.isEmpty()) {
        return
    }

    // Group transactions by category
    val categoryTotals = transactions.groupBy { it.category }
        .mapValues { (_, transactions) -> transactions.sumOf { it.amount }.toFloat() }
    
    val total = categoryTotals.values.sum()
    var showValuesMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Expense Distribution",
                    style = MaterialTheme.typography.titleLarge
                )
                IconButton(onClick = { showValuesMenu = true }) {
                    Icon(Icons.Default.MoreVert, "Show Values")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Pie Chart
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val center = Offset(size.width / 2, size.height / 2)
                    val radius = min(size.width, size.height) / 2 * 0.8f
                    var startAngle = 0f

                    categoryTotals.forEach { (category, amount) ->
                        val sweepAngle = (amount / total) * 360f
                        drawArc(
                            color = ExpenseColors.getColorForCategory(category),
                            startAngle = startAngle,
                            sweepAngle = sweepAngle,
                            useCenter = true,
                            topLeft = Offset(
                                center.x - radius,
                                center.y - radius
                            ),
                            size = Size(radius * 2, radius * 2)
                        )
                        startAngle += sweepAngle
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Legend
            Column {
                categoryTotals.entries
                    .sortedByDescending { it.value }
                    .forEach { (category, amount) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .background(
                                            color = ExpenseColors.getColorForCategory(category),
                                            shape = CircleShape
                                        )
                                )
                                Text(
                                    text = category.name,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            Text(
                                text = "₹${amount.format()} (${((amount / total) * 100).format()}%)",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
            }
        }
    }
}

fun Float.format(): String = String.format("%.1f", this)

@Composable
private fun EmptyExpenseCard(message: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = message,
                color = TextGray,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
fun DailyExpenseChart(transactions: List<Transaction>) {
    val dailyData = remember(transactions) {
        transactions
            .groupBy { transaction ->
                transaction.date.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
            }
            .mapValues { entry -> entry.value.sumOf { it.amount } }
            .toList()
            .sortedBy { it.first }
            .takeLast(7)
    }

    if (dailyData.isEmpty()) {
        EmptyExpenseCard("No daily data available")
        return
    }
    
    ExpenseCard(
        title = "Daily Expenses (Last 7 Days)",
        data = dailyData.map { it.second },
        labels = dailyData.map { it.first.format(DateTimeFormatter.ofPattern("MMM dd")) },
        chartType = ChartType.LINE
    )
}

@Composable
fun WeeklyExpenseChart(transactions: List<Transaction>) {
    val weeklyData = remember(transactions) {
        transactions
            .groupBy { transaction ->
                transaction.date.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
                    .with(java.time.temporal.TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY))
            }
            .mapValues { entry -> entry.value.sumOf { it.amount } }
            .toList()
            .sortedBy { it.first }
            .takeLast(4)
    }

    if (weeklyData.isEmpty()) {
        EmptyExpenseCard("No weekly data available")
        return
    }
    
    ExpenseCard(
        title = "Weekly Expenses (Last 4 Weeks)",
        data = weeklyData.map { it.second },
        labels = weeklyData.mapIndexed { index, _ -> "Week ${weeklyData.size - index}" },
        chartType = ChartType.BAR
    )
}

@Composable
fun MonthlyExpenseChart(transactions: List<Transaction>) {
    val monthlyData = remember(transactions) {
        transactions
            .groupBy { transaction ->
                transaction.date.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
                    .withDayOfMonth(1)
            }
            .mapValues { entry -> entry.value.sumOf { it.amount } }
            .toList()
            .sortedBy { it.first }
            .takeLast(6)
    }

    if (monthlyData.isEmpty()) {
        EmptyExpenseCard("No monthly data available")
        return
    }
    
    ExpenseCard(
        title = "Monthly Expenses (Last 6 Months)",
        data = monthlyData.map { it.second },
        labels = monthlyData.map { it.first.format(DateTimeFormatter.ofPattern("MMM yyyy")) },
        chartType = ChartType.BAR
    )
}

private enum class ChartType {
    LINE, BAR
}

@Composable
private fun ExpenseCard(
    title: String,
    data: List<Double>,
    labels: List<String>,
    chartType: ChartType
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = title,
                color = TextWhite,
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            when (chartType) {
                ChartType.LINE -> CustomLineChart(
                    data = data,
                    labels = labels,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
                ChartType.BAR -> CustomBarChart(
                    data = data,
                    labels = labels,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
            }
        }
    }
}

@Composable
fun CustomLineChart(
    data: List<Double>,
    labels: List<String>,
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer()
    val maxValue = remember(data) { data.maxOrNull() ?: 0.0 }
    val minValue = remember(data) { data.minOrNull() ?: 0.0 }
    val range = remember(maxValue, minValue) { maxValue - minValue }
    
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val padding = 40f
        val chartWidth = width - 2 * padding
        val chartHeight = height - 2 * padding
        
        // Draw grid lines
        val gridLines = 5
        for (i in 0..gridLines) {
            val y = padding + (chartHeight * (1 - i.toFloat() / gridLines))
            drawLine(
                color = TextGray.copy(alpha = 0.3f),
                start = Offset(padding, y),
                end = Offset(width - padding, y),
                strokeWidth = 1f
            )
            
            // Draw Y-axis labels
            val value = minValue + (range * i / gridLines)
            val text = "₹${String.format("%.0f", value)}"
            val textLayoutResult = textMeasurer.measure(
                text = text,
                style = TextStyle(color = TextGray, fontSize = 10.sp)
            )
            drawText(
                textLayoutResult = textLayoutResult,
                topLeft = Offset(0f, y - textLayoutResult.size.height / 2)
            )
        }
        
        // Draw data points and lines
        if (data.isNotEmpty()) {
            val points = data.mapIndexed { index, value ->
                val x = padding + (chartWidth * index / (data.size - 1))
                val y = padding + (chartHeight * (1 - ((value - minValue) / range).toFloat()))
                Offset(x, y)
            }
            
            // Draw lines
            for (i in 0 until points.size - 1) {
                drawLine(
                    color = Purple,
                    start = points[i],
                    end = points[i + 1],
                    strokeWidth = 2f
                )
            }
            
            // Draw points
            points.forEach { point ->
                drawCircle(
                    color = Purple,
                    radius = 4f,
                    center = point
                )
            }
            
            // Draw X-axis labels
            labels.forEachIndexed { index, label ->
                if (index % max(1, labels.size / 4) == 0) {
                    val x = padding + (chartWidth * index / (data.size - 1))
                    val textLayoutResult = textMeasurer.measure(
                        text = label,
                        style = TextStyle(color = TextGray, fontSize = 10.sp)
                    )
                    drawText(
                        textLayoutResult = textLayoutResult,
                        topLeft = Offset(x - textLayoutResult.size.width / 2, height - padding + 5f)
                    )
                }
            }
        }
    }
}

@Composable
fun CustomBarChart(
    data: List<Double>,
    labels: List<String>,
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer()
    val maxValue = remember(data) { data.maxOrNull() ?: 0.0 }
    val minValue = remember(data) { data.minOrNull() ?: 0.0 }
    val range = remember(maxValue, minValue) { maxValue - minValue }
    
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val padding = 40f
        val chartWidth = width - 2 * padding
        val chartHeight = height - 2 * padding
        
        // Draw grid lines
        val gridLines = 5
        for (i in 0..gridLines) {
            val y = padding + (chartHeight * (1 - i.toFloat() / gridLines))
            drawLine(
                color = TextGray.copy(alpha = 0.3f),
                start = Offset(padding, y),
                end = Offset(width - padding, y),
                strokeWidth = 1f
            )
            
            // Draw Y-axis labels
            val value = minValue + (range * i / gridLines)
            val text = "₹${String.format("%.0f", value)}"
            val textLayoutResult = textMeasurer.measure(
                text = text,
                style = TextStyle(color = TextGray, fontSize = 10.sp)
            )
            drawText(
                textLayoutResult = textLayoutResult,
                topLeft = Offset(0f, y - textLayoutResult.size.height / 2)
            )
        }
        
        // Draw bars
        if (data.isNotEmpty()) {
            val barWidth = chartWidth / (data.size * 2)
            val spacing = barWidth / 2
            
            data.forEachIndexed { index, value ->
                val x = padding + spacing + (barWidth + spacing) * index
                val barHeight = ((value - minValue) / range).toFloat() * chartHeight
                val y = padding + chartHeight - barHeight
                
                drawRect(
                    color = Purple,
                    topLeft = Offset(x, y),
                    size = Size(barWidth, barHeight)
                )
                
                // Draw X-axis labels
                if (index % max(1, data.size / 4) == 0) {
                    val textLayoutResult = textMeasurer.measure(
                        text = labels[index],
                        style = TextStyle(color = TextGray, fontSize = 10.sp)
                    )
                    drawText(
                        textLayoutResult = textLayoutResult,
                        topLeft = Offset(x + barWidth / 2 - textLayoutResult.size.width / 2, height - padding + 5f)
                    )
                }
            }
        }
    }
} 