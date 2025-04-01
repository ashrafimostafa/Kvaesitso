package ir.mostafa.launcher.search.data

import ir.mostafa.launcher.search.Searchable
import ir.mostafa.launcher.unitconverter.Dimension
import ir.mostafa.launcher.unitconverter.UnitValue

open class UnitConverter(
    val dimension: Dimension,
    val inputValue: UnitValue,
    val values: List<UnitValue>
): Searchable
