package com.stho.isee.encryption

interface IEncryptor {
    fun encryptString(plainText: String): String
    fun decryptString(cipherText: String): String
}