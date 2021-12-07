package com.stho.isee.utilities

object Splitter {

    fun split(text: String): List<String> =
        text.split(DELIMITER).map { value -> value.replace(DELIMITER_REPLACEMENT, DELIMITER) }

    fun join(texts: Collection<String>): String =
        texts.joinToString(separator = DELIMITER) { value -> value.replace(DELIMITER, DELIMITER_REPLACEMENT) }

    fun join(vararg texts: String): String =
        texts.joinToString(separator = DELIMITER) { value -> value.replace(DELIMITER, DELIMITER_REPLACEMENT) }

    private const val DELIMITER = "::"
    private const val DELIMITER_REPLACEMENT = "{:}"
}

