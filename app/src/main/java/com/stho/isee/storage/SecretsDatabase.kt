package com.stho.isee.storage

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Secret::class], version = 1)
    abstract class SecretsDatabase : RoomDatabase() {
    abstract fun secretDao(): SecretDao
}

