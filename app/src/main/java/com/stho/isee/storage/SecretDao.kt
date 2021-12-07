package com.stho.isee.storage

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface SecretDao {

    @Query("SELECT * FROM secret")
    fun getAll(): List<Secret>

    @Query("SELECT * FROM secret WHERE id IN (:ids)")
    fun loadAllByIds(ids: IntArray): List<Secret>

    @Query("SELECT * FROM secret WHERE id = :id LIMIT 1")
    fun findById(id: Int): Secret

    @Insert
    fun insertAll(vararg secrets: Secret)

    @Delete
    fun delete(secret: Secret)
}