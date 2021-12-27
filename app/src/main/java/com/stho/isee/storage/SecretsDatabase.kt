package com.stho.isee.storage

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Secret::class], version = 1, exportSchema = true)

    abstract class SecretsDatabase : RoomDatabase() {
    abstract fun secretDao(): SecretDao

    companion object {
        fun build(context: Context, databaseName: String = "SecretsDatabase"): SecretsDatabase =
            Room.databaseBuilder(context, SecretsDatabase::class.java, databaseName)
                .fallbackToDestructiveMigration()
                .build()
    }
}

