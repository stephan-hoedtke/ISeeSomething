package com.stho.isee.core

import android.util.Log
import com.stho.isee.encryption.IEncryptor
import com.stho.isee.storage.IStorage
import com.stho.isee.storage.Secret
import com.stho.isee.utilities.Splitter
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.collections.ArrayList

/**
 * Implements conversion between Entry (the viewable object) and Secret (the database object)
 * All methods call the storage methods asynchronously.
 */
class EntryPersister(private val storage: IStorage, private val encryptor: IEncryptor) {

    fun read(): ArrayList<Entry> {
        val entries = ArrayList<Entry>()
        for (secret in readAsync()) {
            try {
                val entry = toEntry(secret)
                entries.add(entry)
            } catch (ex: Exception) {
                Log.d("DB", "Error reading database entries: ${ex.message}") // Ignore...
            }
        }
        return entries
    }

    fun save(entry: Entry) {
        val secret = toSecret(entry)
        if (entry.isNew) {
            val id = insertAsync(secret)
            entry.setId(id)
            entry.setStatus(Entry.Status.CLEAN)
        } else {
            updateAsync(secret)
            entry.setStatus(Entry.Status.CLEAN)
        }
    }

    fun delete(entry: Entry) {
        if (entry.isNotNew) {
            deleteAsync(entry.id)
        }
    }

    private fun readAsync(): List<Secret> =
        runBlocking {
            withContext(Default) {
                storage.readSecrets()
        }
    }

    private fun insertAsync(secret: Secret): Long =
        runBlocking {
            withContext(Default) {
                insertValidate(secret)
            }
        }

    private fun updateAsync(secret: Secret) =
        runBlocking {
            withContext(Default) {
                updateValidate(secret)
            }
        }

    private fun deleteAsync(id: Long) =
        runBlocking {
            withContext(Default) {
                deleteValidate(id)
            }
        }


    private suspend fun insertValidate(secret: Secret): Long {
        if (secret.id > 0L) {
            throw Exception("Error when inserting new entry with a valid id=${secret.id}.")
        }
        if (secret.id < 0L) {
            secret.id = 0L
        }
        val id = storage.insert(secret)
        if (id < 0L) {
            throw Exception("Error when inserting new entry with id=${secret.id}: invalid new row id $id.")
        }
        return id
    }

    private suspend fun updateValidate(secret: Secret) {
        if (secret.id <= 0L) {
            throw Exception("Error when updating the entry with invalid id=${secret.id}.")
        }
        val count = storage.update(secret)
        if (count != 1) {
            throw Exception("Error when updating entry with id=${secret.id}: $count records updated.")
        }
    }

    private suspend fun deleteValidate(id: Long) {
        if (id <= 0L) {
            throw Exception("Error when deleting the entry with invalid id $id.")
        }
        storage.deleteById(id)
    }

    private fun toEntry(secret: Secret): Entry {
        try {
            val id = secret.id
            val value = encryptor.decryptString(secret.value)
            val token = Splitter.split(value)
            if (token.size > 7) {
                val category = token[0]
                val title = token[1]
                val url = token[2]
                val user = token[3]
                val password = token[4]
                val description = token[5]
                val created = Calendar.getInstance().also { it.timeInMillis = token[6].toLong() }
                val modified = Calendar.getInstance().also { it.timeInMillis = token[7].toLong() }
                return Entry.createFromDB(id, category, title, url, user, password, description, created, modified)
            } else {
                throw Exception("Invalid database entry $id")
            }
        } catch (ex: java.lang.Exception) {
            Log.d("IO", "Error when reading entry from database: ${ex.message}.")
            throw ex
        }
    }

    private fun toSecret(entry: Entry): Secret {
        try {
            val id = entry.id
            val value = Splitter.join(
                entry.category,
                entry.title,
                entry.url,
                entry.user,
                entry.password,
                entry.description,
                entry.created.timeInMillis.toString(),
                entry.modified.timeInMillis.toString(),
            )
            return Secret(id, encryptor.encryptString(value))
        } catch (ex: java.lang.Exception) {
            Log.d("IO", "Error when writing entry to database: ${ex.message}.")
            throw ex
        }
    }
}
