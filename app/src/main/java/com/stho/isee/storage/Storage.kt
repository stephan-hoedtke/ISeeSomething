package com.stho.isee.storage

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * read:
 * https://developer.android.com/training/data-storage/room/accessing-data
 */
class Storage(context: Context) : IStorage {

    private val db: SecretsDatabase by lazy { SecretsDatabase.build(context) }
    private val secretDao = db.secretDao()

    override suspend fun readSecrets(): List<Secret>
        = secretDao.getAll()

    override suspend fun insert(secret: Secret): Long
        = secretDao.insert(secret)

    override suspend fun update(secret: Secret)
        = secretDao.update(secret)

    override suspend fun deleteById(id: Long) {
        val secret = secretDao.findById(id)
        secretDao.delete(secret)
    }
}

