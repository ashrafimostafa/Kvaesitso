package ir.mostafa.launcher.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import ir.mostafa.launcher.database.entities.ThemeEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface ThemeDao {
    @Query("SELECT * FROM Theme")
    fun getAll(): Flow<List<ThemeEntity>>

    @Query("SELECT * FROM Theme WHERE id = :id LIMIT 1")
    fun get(id: UUID): Flow<ThemeEntity?>

    @Insert
    suspend fun insert(theme: ThemeEntity)

    @Update
    suspend fun update(theme: ThemeEntity)

    @Query("DELETE FROM Theme WHERE id = :id")
    suspend fun delete(id: UUID)

    @Query("DELETE FROM Theme")
    suspend fun deleteAll()

    @Insert
    fun insertAll(themes: List<ThemeEntity>)
}