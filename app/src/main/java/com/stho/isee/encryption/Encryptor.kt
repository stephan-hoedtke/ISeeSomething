package com.stho.isee.encryption

import android.content.Context
import android.security.keystore.KeyProperties.*
import com.stho.isee.utilities.decodeBase64
import com.stho.isee.utilities.encodeBase64
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec

/**
 * Read: https://proandroiddev.com/secure-data-in-android-initialization-vector-6ca1c659762c
 *
 * Note:
 *  Encryption is using an automatically created initialization vector iv.
 *  This iv vector is required for decryption later. Therefore it is kept along with the data as the first 16 bytes.
 */
class Encryptor(context: Context) : IEncryptor {

    private val keyStoreWrapper: KeyStoreWrapper = KeyStoreWrapper(context)
    private val key = keyStoreWrapper.getSymmetricKey("I.see.it")

    override fun encryptString(plainText: String): String =
        encryptBytes(plainText.encodeToByteArray()).encodeBase64()

    override fun decryptString(cipherText: String): String =
        decryptBytes(cipherText.decodeBase64()).decodeToString()

    /**
     * Encrypt the bytes, then join the iv-vector and the encrypted byte array, the iv vector is required for decryption.
     */
    private fun encryptBytes(bytes: ByteArray): ByteArray {
        val cipher = getCipher().apply {
            init(Cipher.ENCRYPT_MODE, key)
            assert(iv.size == 16) { "Invalid iv: 16 bytes expected." }
        }
        return encryptBytes(iv = cipher.iv, encryptedBytes = cipher.doFinal(bytes))
    }

    /**
     * Join the iv-vector and the encrypted byte array, the iv vector will is required for decryption.
     */
    private fun encryptBytes(iv: ByteArray, encryptedBytes: ByteArray): ByteArray =
        iv.plus(encryptedBytes)

    /**
     * Split the data into the iv vector (first 16 bytes = 128 bits) and the encrypted byte array, then decrypt using the iv vector.
     */
    private fun decryptBytes(bytes: ByteArray): ByteArray =
        decryptBytes(iv = bytes.copyOfRange(0, 16), encryptedBytes = bytes.copyOfRange(16, bytes.size))

    private fun decryptBytes(iv: ByteArray, encryptedBytes: ByteArray): ByteArray {
        val cipher = getCipher().apply {
            init(Cipher.DECRYPT_MODE, key, IvParameterSpec(iv))
        }
        return cipher.doFinal(encryptedBytes)
    }

    private fun getCipher(): Cipher =
        Cipher.getInstance(
            KEY_ALGORITHM_AES + "/"
                    + BLOCK_MODE_CBC + "/"
                    + ENCRYPTION_PADDING_PKCS7
        )
}

