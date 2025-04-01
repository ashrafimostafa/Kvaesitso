package ir.mostafa.launcher.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import ir.mostafa.launcher.database.entities.SearchActionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SearchActionDao {
    @Query("SELECT * FROM SearchAction ORDER BY position ASC")
    fun getSearchActions(): Flow<List<SearchActionEntity>>

    @Transaction
    suspend fun replaceAll(actions: List<SearchActionEntity>) {
        deleteAll()
        insertAll(actions)
    }

    @Query("DELETE FROM `SearchAction`")
    suspend fun deleteAll()

    @Insert
    suspend fun insertAll(actions: List<SearchActionEntity>)
}