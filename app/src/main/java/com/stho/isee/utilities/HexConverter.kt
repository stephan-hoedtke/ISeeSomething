package com.stho.isee.utilities

object HexConverter {

    fun toHex(bytes: ByteArray): String =
        bytes.joinToString("") {
            toHexByte(it)
        }

    fun decodeHex(value: String): ByteArray {
        val l = value.length / 2
        return ByteArray(l) {
            val i = 2 * it
            val high = decodeHexByte(value[i])
            val low = decodeHexByte(value[i + 1])
            (high * 16 + low).toByte()
        }
    }

    private fun toHexByte(byte: Byte): String =
        byte.toString(16).padStart(2, '0')

    private fun decodeHexByte(value: Char): Int =
        value.digitToInt(16)

}

fun ByteArray.toHex(): String =
    HexConverter.toHex(this)

fun String.decodeHex(): ByteArray =
    HexConverter.decodeHex(this)

