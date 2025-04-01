package ir.mostafa.launcher.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Currency")
data class CurrencyEntity(
        @PrimaryKey val symbol: String,
        val value: Double,
        val lastUpdate: Long
)