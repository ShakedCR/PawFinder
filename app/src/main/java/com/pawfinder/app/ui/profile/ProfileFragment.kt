package com.pawfinder.app.ui.profile

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.pawfinder.app.R

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private lateinit var auth: FirebaseAuth
    private lateinit var tvProfileName: TextView
    private lateinit var tvProfileEmail: TextView
    private lateinit var btnLogout: MaterialButton

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()

        initViews(view)
        bindCurrentUser()
        setupClickListeners()
    }

    private fun initViews(view: View) {
        tvProfileName = view.findViewById(R.id.tvProfileName)
        tvProfileEmail = view.findViewById(R.id.tvProfileEmail)
        btnLogout = view.findViewById(R.id.btnLogout)
    }

    private fun bindCurrentUser() {
        val currentUser = auth.currentUser

        tvProfileEmail.text = currentUser?.email ?: "No email available"
        tvProfileName.text = currentUser?.displayName ?: "PawFinder User"
    }

    private fun setupClickListeners() {
        btnLogout.setOnClickListener {
            logoutUser()
        }
    }

    private fun logoutUser() {
        auth.signOut()
        findNavController().navigate(R.id.loginFragment)
    }
}