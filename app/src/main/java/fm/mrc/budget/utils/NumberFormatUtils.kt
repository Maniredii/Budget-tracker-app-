package fm.mrc.budget.utils

import java.text.DecimalFormat

fun Double.format(): String {
    val formatter = DecimalFormat("#,##,###.##")
    return formatter.format(this)
} 