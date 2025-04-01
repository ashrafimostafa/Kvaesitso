package ir.mostafa.launcher.search.data

import ir.mostafa.launcher.unitconverter.Dimension
import ir.mostafa.launcher.unitconverter.UnitValue

class CurrencyUnitConverter(dimension: Dimension, inputValue: UnitValue, values: List<UnitValue>, val updateTimestamp: Long)
    : UnitConverter(dimension, inputValue, values)