package com.stho.isee.ui.details

import android.text.method.PasswordTransformationMethod
import android.view.View

class DisplayPasswordHintTransformationMethod(private val visibleCharIndex: Int): PasswordTransformationMethod() {

    override fun getTransformation(source: CharSequence, view: View): CharSequence {
        return MyPasswordCharSequence(source)
    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        super.onTextChanged(s, start, before, count) // Return default
    }

    private inner class MyPasswordCharSequence(private val source: CharSequence) : CharSequence {

        override val length: Int
            get() = source.length // Return default

        override fun get(index: Int): Char {
            return if (index == visibleCharIndex) source[index] else '•'
        }

        override fun subSequence(startIndex: Int, endIndex: Int): CharSequence {
            return source.subSequence(startIndex, endIndex) // Return default
        }
    }
}
