package com.stho.isee.storage

import android.content.Context
import androidx.room.Room

class Storage(applicationContext: Context) : IStorage {

    private val db = Room.databaseBuilder(applicationContext, SecretsDatabase::class.java, "SecretsDatabase").build()
    private val secretDao = db.secretDao()

    override fun readSecrets(): List<Secret>
        = secretDao.getAll()

    override fun save(secret: Secret)
        = secretDao.insertAll(secret)
}
