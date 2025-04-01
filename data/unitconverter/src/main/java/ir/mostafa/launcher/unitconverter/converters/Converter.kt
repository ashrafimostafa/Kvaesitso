package ir.mostafa.launcher.unitconverter.converters

import android.content.Context
import ir.mostafa.launcher.search.data.UnitConverter
import ir.mostafa.launcher.unitconverter.Dimension
import ir.mostafa.launcher.unitconverter.MeasureUnit

interface Converter {
    val dimension: Dimension

    suspend fun isValidUnit(symbol: String): Boolean

    suspend fun convert(
        context: Context,
        fromUnit: String,
        value: Double,
        toUnit: String?
    ): UnitConverter

    suspend fun getSupportedUnits(): List<MeasureUnit>
}

