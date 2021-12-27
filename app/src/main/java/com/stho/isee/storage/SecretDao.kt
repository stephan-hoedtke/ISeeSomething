package com.stho.isee.storage

import androidx.room.*
import java.util.*

@Dao
interface SecretDao {

    @Query("SELECT * FROM secrets ORDER BY id")
    suspend fun getAll(): List<Secret>

    @Query("SELECT * FROM secrets WHERE id = :id LIMIT 1")
    suspend fun findById(id: Long): Secret

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(secret: Secret): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(secret: Secret): Int

    @Delete
    suspend fun delete(secret: Secret)
}


