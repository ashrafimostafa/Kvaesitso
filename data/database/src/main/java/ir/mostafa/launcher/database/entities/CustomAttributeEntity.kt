package ir.mostafa.launcher.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "CustomAttributes")
data class CustomAttributeEntity(
    val key: String,
    val type: String,
    val value: String,
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
)