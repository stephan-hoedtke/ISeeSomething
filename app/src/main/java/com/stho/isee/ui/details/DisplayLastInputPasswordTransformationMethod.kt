package com.stho.isee.ui.details

import android.text.method.PasswordTransformationMethod
import android.view.View

/**
 * This method shows the last character when a password was entered.
 * It shall be used instead of the default: PasswordTransformationMethod.getInstance() for passwords or
 */
class DisplayLastInputPasswordTransformationMethod : PasswordTransformationMethod() {

    var lastActionWasDelete = false

    override fun getTransformation(source: CharSequence, view: View): CharSequence {
        return MyPasswordCharSequence(source)
    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        super.onTextChanged(s, start, before, count)
        lastActionWasDelete = before > count
    }

    private inner class MyPasswordCharSequence(private val source: CharSequence) : CharSequence {

        override val length: Int
            get() = source.length // Return default

        override fun get(index: Int): Char {
            //This is the check which makes sure the last character is shown
            return if (!lastActionWasDelete && index == source.length - 1) source[index] else '•'
        }

        override fun subSequence(startIndex: Int, endIndex: Int): CharSequence {
            return source.subSequence(startIndex, endIndex) // Return default
        }
    }
}
