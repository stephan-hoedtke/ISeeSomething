package com.stho.isee.core

import android.util.Log
import com.stho.isee.encryption.IEncryptor
import com.stho.isee.storage.IStorage
import com.stho.isee.storage.Secret
import com.stho.isee.utilities.Splitter
import java.util.*
import kotlin.collections.ArrayList

/**
 * See: https://developer.android.com/training/data-storage/room
 */
class EntryPersister(private val storage: IStorage, private val encryptor: IEncryptor) {

    fun read(): List<Entry> {
        val entries = ArrayList<Entry>()
        for (secret in storage.readSecrets()) {
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
        storage.save(secret)
    }

    private fun toEntry(secret: Secret): Entry {
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
    }

    private fun toSecret(entry: Entry): Secret {
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
    }

    companion object {
        private const val DELIMITER = "::"
        private const val DELIMITER_REPLACEMENT = "{:}"
    }
}
