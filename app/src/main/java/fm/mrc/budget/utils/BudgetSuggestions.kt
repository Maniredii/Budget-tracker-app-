package fm.mrc.budget.utils

import java.util.*

object BudgetSuggestions {
    private var previousExpenses: Map<String, Double> = emptyMap()
    private var lastUpdateTime: Date? = null

    fun getOfflineBudgetAdvice(
        monthlyIncome: Double,
        currentExpenses: Double,
        expenseCategories: Map<String, Double>
    ): String {
        val savingsRate = ((monthlyIncome - currentExpenses) / monthlyIncome * 100).coerceAtLeast(0.0)
        val highestExpenseCategory = expenseCategories.maxByOrNull { it.value }
        val expenseRatio = (currentExpenses / monthlyIncome * 100).coerceAtLeast(0.0)

        // Calculate changes in expenses
        val changes = calculateExpenseChanges(expenseCategories)
        
        val suggestions = StringBuilder()
        suggestions.append("📊 Budget Analysis:\n\n")

        // Basic financial health check
        suggestions.append("Current Financial Status:\n")
        suggestions.append("• Monthly Income: ₹${monthlyIncome.format()}\n")
        suggestions.append("• Total Expenses: ₹${currentExpenses.format()}\n")
        suggestions.append("• Savings Rate: ${savingsRate.format()}%\n\n")

        // Expense analysis with trends
        suggestions.append("💡 Key Observations:\n")
        if (highestExpenseCategory != null) {
            suggestions.append("• Your highest expense category is ${highestExpenseCategory.key} at ₹${highestExpenseCategory.value.format()}\n")
        }
        suggestions.append("• You're spending ${expenseRatio.format()}% of your income\n")
        
        // Add trend analysis if available
        if (changes.isNotEmpty()) {
            suggestions.append("\n📈 Recent Changes:\n")
            changes.forEach { (category, change) ->
                val trend = if (change > 0) "increased" else "decreased"
                val emoji = if (change > 0) "⚠️" else "✅"
                suggestions.append("$emoji $category has $trend by ₹${Math.abs(change).format()}\n")
            }
        }

        // General recommendations based on current state
        suggestions.append("\n🎯 Recommendations:\n")
        suggestions.append(getRecommendations(expenseRatio, savingsRate, changes))
        
        // Category-specific advice
        suggestions.append("\n📋 Category-Specific Tips:\n")
        suggestions.append(getCategorySpecificTips(expenseCategories, changes))

        // Update previous expenses for next comparison
        previousExpenses = expenseCategories
        lastUpdateTime = Date()

        return suggestions.toString()
    }

    private fun calculateExpenseChanges(currentExpenses: Map<String, Double>): Map<String, Double> {
        if (previousExpenses.isEmpty()) return emptyMap()
        
        return currentExpenses.mapNotNull { (category, amount) ->
            val previousAmount = previousExpenses[category] ?: return@mapNotNull null
            val change = amount - previousAmount
            if (Math.abs(change) > 0.01) { // Only show significant changes
                category to change
            } else null
        }.toMap()
    }

    private fun getRecommendations(
        expenseRatio: Double,
        savingsRate: Double,
        changes: Map<String, Double>
    ): String {
        val recommendations = StringBuilder()
        
        // Basic recommendations based on expense ratio
        when {
            expenseRatio > 90 -> {
                recommendations.append("• ⚠️ Your expenses are very high relative to income\n")
                recommendations.append("• Consider immediate expense reduction\n")
                recommendations.append("• Look for additional income sources\n")
            }
            expenseRatio > 70 -> {
                recommendations.append("• Try to reduce non-essential expenses\n")
                recommendations.append("• Aim to save at least 20% of your income\n")
                recommendations.append("• Review and cancel unused subscriptions\n")
            }
            else -> {
                recommendations.append("• Good job managing expenses!\n")
                recommendations.append("• Consider investing your savings\n")
                recommendations.append("• Build an emergency fund if not already done\n")
            }
        }

        // Savings-based recommendations
        when {
            savingsRate < 10 -> {
                recommendations.append("• Start with small savings goals\n")
                recommendations.append("• Use automatic savings transfers\n")
            }
            savingsRate > 30 -> {
                recommendations.append("• Consider long-term investments\n")
                recommendations.append("• Maintain your excellent saving habits\n")
            }
        }

        // Trend-based recommendations
        val increasedCategories = changes.filter { it.value > 0 }.keys
        if (increasedCategories.isNotEmpty()) {
            recommendations.append("\n📊 Based on recent changes:\n")
            recommendations.append("• Focus on reducing expenses in: ${increasedCategories.joinToString(", ")}\n")
        }

        return recommendations.toString()
    }

    private fun getCategorySpecificTips(
        expenseCategories: Map<String, Double>,
        changes: Map<String, Double>
    ): String {
        val tips = StringBuilder()
        
        expenseCategories.forEach { (category, amount) ->
            val change = changes[category]
            val isIncreasing = change != null && change > 0
            
            when (category.uppercase()) {
                "FOOD" -> {
                    tips.append("• Food:")
                    if (isIncreasing) {
                        tips.append(" ⚠️ Spending increasing! Try meal planning and bulk buying\n")
                    } else {
                        tips.append(" Cook meals at home, plan weekly menus, buy in bulk\n")
                    }
                }
                "TRANSPORTATION" -> {
                    tips.append("• Transportation:")
                    if (isIncreasing) {
                        tips.append(" ⚠️ Consider carpooling or public transport to reduce costs\n")
                    } else {
                        tips.append(" Look for fuel-efficient routes and maintain your vehicle\n")
                    }
                }
                "SHOPPING" -> {
                    tips.append("• Shopping:")
                    if (isIncreasing) {
                        tips.append(" ⚠️ Track your purchases and stick to essentials\n")
                    } else {
                        tips.append(" Make a list, wait for sales, avoid impulse purchases\n")
                    }
                }
                "ENTERTAINMENT" -> {
                    tips.append("• Entertainment:")
                    if (isIncreasing) {
                        tips.append(" ⚠️ Look for free activities and entertainment options\n")
                    } else {
                        tips.append(" Look for free local events, use streaming services wisely\n")
                    }
                }
                "UTILITIES" -> {
                    tips.append("• Utilities:")
                    if (isIncreasing) {
                        tips.append(" ⚠️ Check for energy leaks and optimize usage\n")
                    } else {
                        tips.append(" Use energy-efficient appliances, monitor usage\n")
                    }
                }
                "HEALTH" -> {
                    tips.append("• Health:")
                    if (isIncreasing) {
                        tips.append(" ⚠️ Review insurance coverage and preventive care options\n")
                    } else {
                        tips.append(" Consider preventive care, compare insurance options\n")
                    }
                }
                "EDUCATION" -> {
                    tips.append("• Education:")
                    if (isIncreasing) {
                        tips.append(" ⚠️ Look for scholarships and financial aid options\n")
                    } else {
                        tips.append(" Look for scholarships, online courses, free resources\n")
                    }
                }
                "TRAVEL" -> {
                    tips.append("• Travel:")
                    if (isIncreasing) {
                        tips.append(" ⚠️ Plan ahead and look for travel deals\n")
                    } else {
                        tips.append(" Book in advance, use travel rewards, compare prices\n")
                    }
                }
                "HOUSING" -> {
                    tips.append("• Housing:")
                    if (isIncreasing) {
                        tips.append(" ⚠️ Review utilities and maintenance costs\n")
                    } else {
                        tips.append(" Regular maintenance can prevent costly repairs\n")
                    }
                }
            }
        }

        return tips.toString()
    }

    private fun Double.format(): String = String.format("%.2f", this)
} 