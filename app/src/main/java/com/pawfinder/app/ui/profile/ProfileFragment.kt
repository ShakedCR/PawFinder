package com.pawfinder.app.ui.profile

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.pawfinder.app.R
import com.pawfinder.app.data.local.DatabaseProvider
import com.pawfinder.app.data.repository.UserRepository

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private lateinit var auth: FirebaseAuth
    private lateinit var userViewModel: UserViewModel

    private lateinit var tvProfileName: TextView
    private lateinit var tvProfileEmail: TextView
    private lateinit var ivProfileImage: ImageView
    private lateinit var btnLogout: MaterialButton
    private lateinit var btnEditProfile: MaterialButton

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()
        initViewModel()
        initViews(view)
        loadUserData()
        setupClickListeners()
    }

    private fun initViewModel() {
        val database = DatabaseProvider.getDatabase(requireContext())
        val repository = UserRepository(database.userDao())
        val factory = UserViewModelFactory(repository)
        userViewModel = ViewModelProvider(this, factory)[UserViewModel::class.java]
    }

    private fun initViews(view: View) {
        tvProfileName = view.findViewById(R.id.tvProfileName)
        tvProfileEmail = view.findViewById(R.id.tvProfileEmail)
        ivProfileImage = view.findViewById(R.id.ivProfileImage)
        btnLogout = view.findViewById(R.id.btnLogout)
        btnEditProfile = view.findViewById(R.id.btnEditProfile)
    }

    private fun loadUserData() {
        val currentUser = auth.currentUser ?: return
        tvProfileEmail.text = currentUser.email ?: "No email available"

        userViewModel.loadUserById(currentUser.uid)
        userViewModel.user.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                tvProfileName.text = user.name
                if (user.profileImageUrl.isNotBlank()) {
                    Glide.with(this)
                        .load(user.profileImageUrl)
                        .circleCrop()
                        .placeholder(R.drawable.ic_profile)
                        .into(ivProfileImage)
                }
            } else {
                tvProfileName.text = "PawFinder User"
            }
        }
    }

    private fun setupClickListeners() {
        btnEditProfile.setOnClickListener {
            findNavController().navigate(R.id.editProfileFragment)
        }

        btnLogout.setOnClickListener {
            logoutUser()
        }
    }

    private fun logoutUser() {
        auth.signOut()
        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.loginFragment, true)
            .build()
        findNavController().navigate(R.id.loginFragment, null, navOptions)
    }
}