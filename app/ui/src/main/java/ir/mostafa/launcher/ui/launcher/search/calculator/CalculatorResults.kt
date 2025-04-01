package ir.mostafa.launcher.ui.launcher.search.calculator

import androidx.compose.foundation.lazy.LazyListScope
import ir.mostafa.launcher.search.data.Calculator
import ir.mostafa.launcher.ui.launcher.search.common.list.ListItemSurface

fun LazyListScope.CalculatorResults(
    calculator: List<Calculator>,
    reverse: Boolean,
) {
    if (calculator.isNotEmpty()) {
        item(key = "calculator") {
            ListItemSurface(
                isFirst = true,
                isLast = true,
                reverse = reverse,
            ) {
                CalculatorItem(calculator = calculator.first())
            }
        }
    }
}