package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CreationDao {

    @Query("SELECT * FROM creations ORDER BY timestamp DESC")
    fun getAllCreations(): Flow<List<CreationEntity>>

    @Query("SELECT * FROM creations WHERE isVideo = 0 ORDER BY timestamp DESC")
    fun getImageCreations(): Flow<List<CreationEntity>>

    @Query("SELECT * FROM creations WHERE isVideo = 1 ORDER BY timestamp DESC")
    fun getVideoCreations(): Flow<List<CreationEntity>>

    @Query("SELECT * FROM creations WHERE isFavorite = 1 ORDER BY timestamp DESC")
    fun getFavoriteCreations(): Flow<List<CreationEntity>>

    @Query("SELECT * FROM creations WHERE id = :id")
    suspend fun getCreationById(id: Long): CreationEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCreation(creation: CreationEntity): Long

    @Update
    suspend fun updateCreation(creation: CreationEntity)

    @Query("UPDATE creations SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavorite(id: Long, isFavorite: Boolean)

    @Delete
    suspend fun deleteCreation(creation: CreationEntity)

    @Query("DELETE FROM creations WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM creations")
    suspend fun deleteAll()
}
