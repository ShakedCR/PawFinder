package com.pawfinder.app.ui.auth

import com.pawfinder.app.R
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

class RegisterFragment : Fragment(R.layout.fragment_register) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupClickListeners(view)
    }

    private fun setupClickListeners(view: View) {
        val goToLoginText = view.findViewById<View>(R.id.tvGoToLogin)

        goToLoginText.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }
    }
}