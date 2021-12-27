package com.stho.isee.authentication

import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar

class AuthenticationHandler(
    private val fragment: Fragment,
    private val onAuthentication: (result: AuthenticationResult) -> Unit,
) {
    private val context = fragment.requireContext()
    private val view = fragment.requireView()

    fun confirmAuthentication() {
        val executor = ContextCompat.getMainExecutor(context)
        val biometricPrompt = BiometricPrompt(fragment, executor, object : BiometricPrompt.AuthenticationCallback() {

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                showErrorMessage("Authentication error: $errString")
                onAuthentication(AuthenticationResult.Error)
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                onAuthentication(AuthenticationResult.OK)
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                showErrorMessage("Authentication failed")
                onAuthentication(AuthenticationResult.Cancel)
            }
        })

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric login for my app")
            .setSubtitle("Log in using your biometric credential")
            // Can't call setNegativeButtonText() and
            // setAllowedAuthenticators(... or DEVICE_CREDENTIAL) at the same time.
            // .setNegativeButtonText("Use account password")
            .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL)
            .setConfirmationRequired(false)
            .build()

        // Prompt appears when user clicks "Log in".
        // Consider integrating with the keystore to unlock cryptographic operations,
        // if needed by your app.
        biometricPrompt.authenticate(promptInfo)
    }

    fun showErrorMessage(message: String) {
        Snackbar.make(view, message, Snackbar.LENGTH_INDEFINITE).apply {
            setAction("Dismiss") { dismiss() }
            show()
        }
    }



}