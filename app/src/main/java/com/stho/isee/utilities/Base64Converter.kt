package com.stho.isee.utilities

import android.util.Base64

fun ByteArray.encodeBase64(): String =
    Base64.encodeToString(this, Base64.DEFAULT)

fun String.decodeBase64(): ByteArray =
    Base64.decode(this, Base64.DEFAULT)

