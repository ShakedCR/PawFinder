package com.pawfinder.app.ui.auth

import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.pawfinder.app.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class RegisterFragment : Fragment(R.layout.fragment_register) {

    private lateinit var tilFullName: TextInputLayout
    private lateinit var tilEmail: TextInputLayout
    private lateinit var tilPassword: TextInputLayout
    private lateinit var tilConfirmPassword: TextInputLayout

    private lateinit var etFullName: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var etConfirmPassword: TextInputEditText

    private lateinit var btnRegister: MaterialButton
    private lateinit var tvGoToLogin: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        setupClickListeners()
    }

    private fun initViews(view: View) {
        tilFullName = view.findViewById(R.id.tilRegisterFullName)
        tilEmail = view.findViewById(R.id.tilRegisterEmail)
        tilPassword = view.findViewById(R.id.tilRegisterPassword)
        tilConfirmPassword = view.findViewById(R.id.tilRegisterConfirmPassword)

        etFullName = view.findViewById(R.id.etRegisterFullName)
        etEmail = view.findViewById(R.id.etRegisterEmail)
        etPassword = view.findViewById(R.id.etRegisterPassword)
        etConfirmPassword = view.findViewById(R.id.etRegisterConfirmPassword)

        btnRegister = view.findViewById(R.id.btnRegister)
        tvGoToLogin = view.findViewById(R.id.tvGoToLogin)
    }

    private fun setupClickListeners() {
        tvGoToLogin.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }

        btnRegister.setOnClickListener {
            handleRegisterClick()
        }
    }

    private fun handleRegisterClick() {
        clearErrors()

        val fullName = etFullName.text?.toString()?.trim().orEmpty()
        val email = etEmail.text?.toString()?.trim().orEmpty()
        val password = etPassword.text?.toString()?.trim().orEmpty()
        val confirmPassword = etConfirmPassword.text?.toString()?.trim().orEmpty()

        if (!validateRegisterInput(fullName, email, password, confirmPassword)) {
            return
        }

        Toast.makeText(requireContext(), "Register validation passed", Toast.LENGTH_SHORT).show()
    }

    private fun validateRegisterInput(
        fullName: String,
        email: String,
        password: String,
        confirmPassword: String
    ): Boolean {
        var isValid = true

        if (fullName.isBlank()) {
            tilFullName.error = "Full name is required"
            isValid = false
        }

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

        if (confirmPassword.isBlank()) {
            tilConfirmPassword.error = "Please confirm your password"
            isValid = false
        } else if (password != confirmPassword) {
            tilConfirmPassword.error = "Passwords do not match"
            isValid = false
        }

        return isValid
    }

    private fun clearErrors() {
        tilFullName.error = null
        tilEmail.error = null
        tilPassword.error = null
        tilConfirmPassword.error = null
    }
}