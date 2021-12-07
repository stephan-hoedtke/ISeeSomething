package com.stho.isee.encryption

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyStore
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

/**
 * To manage the symmetric key for encryption.
 * The key is stored inside the secure Android Key Store.
 *
 * See for a good explanation:
 * https://proandroiddev.com/secure-data-in-android-encryption-7eda33e68f58
 * https://proandroiddev.com/secure-data-in-android-encryption-in-android-part-2-991a89e55a23
 * https://proandroiddev.com/secure-data-in-android-encrypting-large-data-dda256a55b36
 */
class KeyStoreWrapper(private val context: Context) {

    private val keyStore = KeyStore.getInstance(ANDROID_KEY_STORE).apply { load(null) }

    /**
     * Get a symmetric key for the alias from the Android Key Store.
     * If it doesn't exist, then create it.
     */
    fun getSymmetricKey(alias: String): SecretKey {
        val key: SecretKey? = keyStore.getKey(alias, null) as SecretKey?
        return key ?: createSymmetricKey(alias)
    }

    /**
     * Create symmetric key and keep it in the Android Key Store
     * (requires API >= 23)
     */
    private fun createSymmetricKey(alias: String): SecretKey {
        val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEY_STORE)
        val builder = KeyGenParameterSpec.Builder(alias, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
            .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
        keyGenerator.init(builder.build())
        return keyGenerator.generateKey()
    }

    companion object {
        private const val ANDROID_KEY_STORE = "AndroidKeyStore"
    }
}

