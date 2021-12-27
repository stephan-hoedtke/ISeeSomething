package com.stho.isee.storage

interface IStorage {
    suspend fun readSecrets(): List<Secret>
    suspend fun insert(secret: Secret): Long
    suspend fun update(secret: Secret): Int
    suspend fun deleteById(id: Long)
}