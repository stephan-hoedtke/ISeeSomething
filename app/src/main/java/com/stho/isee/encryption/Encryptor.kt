package com.stho.isee.encryption

import android.content.Context
import com.stho.isee.utilities.decodeBase64
import com.stho.isee.utilities.encodeBase64
import javax.crypto.Cipher
import javax.crypto.SecretKey

class Encryptor(context: Context) : IEncryptor {

    private val keyStoreWrapper: KeyStoreWrapper = KeyStoreWrapper(context)
    private val secret = keyStoreWrapper.getSymmetricKey("I.see.it")

    override fun encryptString(plainText: String): String =
        encryptByteArray(plainText.toByteArray(Charsets.UTF_8), secret).encodeBase64()

    override fun decryptString(cipherText: String): String =
        decryptByteArray(cipherText.decodeBase64(), secret).toString(Charsets.UTF_8)

    private fun encryptByteArray(plainText: ByteArray, secret: SecretKey): ByteArray =
        Cipher.getInstance("AES/CBC/PKCS5PADDING").let {
            it.init(Cipher.ENCRYPT_MODE, secret)
            it.doFinal(plainText)
        }

    private fun decryptByteArray(cipherText: ByteArray, secret: SecretKey): ByteArray =
        Cipher.getInstance("AES/CBC/PKCS5PADDING").let {
            it.init(Cipher.DECRYPT_MODE, secret)
            it.doFinal(cipherText)
        }
}

