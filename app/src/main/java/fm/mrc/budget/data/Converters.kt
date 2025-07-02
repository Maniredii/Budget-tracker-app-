package fm.mrc.budget.data

import androidx.room.TypeConverter
import fm.mrc.budget.ExpenseCategory
import fm.mrc.budget.PaymentMethod
import java.util.Date

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun fromExpenseCategory(value: ExpenseCategory?): String? {
        return value?.name
    }

    @TypeConverter
    fun toExpenseCategory(value: String?): ExpenseCategory? {
        return value?.let { ExpenseCategory.valueOf(it) }
    }

    @TypeConverter
    fun fromPaymentMethod(value: PaymentMethod?): String? {
        return value?.name
    }

    @TypeConverter
    fun toPaymentMethod(value: String?): PaymentMethod? {
        return value?.let { PaymentMethod.valueOf(it) }
    }
} 