package com.pawfinder.app.ui.auth

import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.pawfinder.app.R

class LoginFragment : Fragment(R.layout.fragment_login) {

    private lateinit var auth: FirebaseAuth

    private lateinit var tilEmail: TextInputLayout
    private lateinit var tilPassword: TextInputLayout
    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var btnLogin: MaterialButton
    private lateinit var tvGoToRegister: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()

        initViews(view)
        setupClickListeners()
    }

    override fun onStart() {
        super.onStart()
        redirectIfUserAlreadyLoggedIn()
    }

    private fun initViews(view: View) {
        tilEmail = view.findViewById(R.id.tilLoginEmail)
        tilPassword = view.findViewById(R.id.tilLoginPassword)
        etEmail = view.findViewById(R.id.etLoginEmail)
        etPassword = view.findViewById(R.id.etLoginPassword)
        btnLogin = view.findViewById(R.id.btnLogin)
        tvGoToRegister = view.findViewById(R.id.tvGoToRegister)
    }

    private fun setupClickListeners() {
        tvGoToRegister.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        btnLogin.setOnClickListener {
            handleLoginClick()
        }
    }

    private fun redirectIfUserAlreadyLoggedIn() {
        val currentUser = auth.currentUser

        if (currentUser != null) {
            findNavController().navigate(R.id.action_loginFragment_to_feedFragment)
        }
    }

    private fun handleLoginClick() {
        clearErrors()

        val email = etEmail.text?.toString()?.trim().orEmpty()
        val password = etPassword.text?.toString()?.trim().orEmpty()

        if (!validateLoginInput(email, password)) {
            return
        }

        loginUser(email, password)
    }

    private fun validateLoginInput(email: String, password: String): Boolean {
        var isValid = true

        if (email.isBlank()) {
            tilEmail.error = "Email is required"
            isValid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.error = "Enter a valid email address"
            isValid = false
        }

        if (password.isBlank()) {
            tilPassword.error = "Password is required"
            isValid = false
        } else if (password.length < 6) {
            tilPassword.error = "Password must be at least 6 characters"
            isValid = false
        }

        return isValid
    }

    private fun loginUser(email: String, password: String) {
        btnLogin.isEnabled = false

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                btnLogin.isEnabled = true

                if (task.isSuccessful) {
                    Toast.makeText(requireContext(), "Login successful", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_loginFragment_to_feedFragment)
                } else {
                    Toast.makeText(
                        requireContext(),
                        task.exception?.localizedMessage ?: "Login failed",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }

    private fun clearErrors() {
        tilEmail.error = null
        tilPassword.error = null
    }
}