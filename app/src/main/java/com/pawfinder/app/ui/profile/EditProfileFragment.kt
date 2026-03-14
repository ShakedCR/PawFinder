package com.pawfinder.app.ui.profile

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.pawfinder.app.R
import com.pawfinder.app.data.local.DatabaseProvider
import com.pawfinder.app.data.repository.UserRepository
import com.pawfinder.app.model.User
import com.pawfinder.app.utils.CloudinaryManager
import android.widget.ImageView

class EditProfileFragment : Fragment(R.layout.fragment_edit_profile) {

    private lateinit var auth: FirebaseAuth
    private lateinit var userViewModel: UserViewModel

    private lateinit var ivEditProfileImage: ImageView
    private lateinit var btnChangeImage: MaterialButton
    private lateinit var tilEditName: TextInputLayout
    private lateinit var etEditName: TextInputEditText
    private lateinit var btnSaveProfile: MaterialButton
    private lateinit var progressIndicator: CircularProgressIndicator

    private var selectedImageUri: Uri? = null
    private var currentImageUrl: String = ""

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            selectedImageUri = uri
            Glide.with(this).load(uri).circleCrop().into(ivEditProfileImage)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()
        initViewModel()
        initViews(view)
        loadCurrentUser()
        setupClickListeners()
    }

    private fun initViewModel() {
        val database = DatabaseProvider.getDatabase(requireContext())
        val repository = UserRepository(database.userDao())
        val factory = UserViewModelFactory(repository)
        userViewModel = ViewModelProvider(this, factory)[UserViewModel::class.java]
    }

    private fun initViews(view: View) {
        ivEditProfileImage = view.findViewById(R.id.ivEditProfileImage)
        btnChangeImage = view.findViewById(R.id.btnChangeImage)
        tilEditName = view.findViewById(R.id.tilEditName)
        etEditName = view.findViewById(R.id.etEditName)
        btnSaveProfile = view.findViewById(R.id.btnSaveProfile)
        progressIndicator = view.findViewById(R.id.progressIndicator)
    }

    private fun loadCurrentUser() {
        val currentUser = auth.currentUser ?: return
        userViewModel.loadUserById(currentUser.uid)
        userViewModel.user.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                etEditName.setText(user.name)
                currentImageUrl = user.profileImageUrl
                if (user.profileImageUrl.isNotBlank()) {
                    Glide.with(this)
                        .load(user.profileImageUrl)
                        .circleCrop()
                        .placeholder(R.drawable.ic_profile)
                        .into(ivEditProfileImage)
                }
            }
        }
    }

    private fun setupClickListeners() {
        btnChangeImage.setOnClickListener {
            pickImageLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        }

        btnSaveProfile.setOnClickListener {
            handleSaveProfile()
        }
    }

    private fun handleSaveProfile() {
        val name = etEditName.text?.toString()?.trim().orEmpty()

        if (name.isBlank()) {
            tilEditName.error = "Name is required"
            return
        }

        tilEditName.error = null
        setLoading(true)

        if (selectedImageUri != null) {
            CloudinaryManager.uploadProfileImage(
                context = requireContext(),
                imageUri = selectedImageUri!!,
                onSuccess = { url ->
                    saveUser(name, url)
                },
                onError = {
                    setLoading(false)
                    Snackbar.make(requireView(), "Image upload failed", Snackbar.LENGTH_LONG).show()
                }
            )
        } else {
            saveUser(name, currentImageUrl)
        }
    }

    private fun saveUser(name: String, imageUrl: String) {
        val currentUser = auth.currentUser ?: return
        val user = User(
            id = currentUser.uid,
            name = name,
            email = currentUser.email ?: "",
            profileImageUrl = imageUrl
        )
        userViewModel.insertUser(user)
        setLoading(false)
        Snackbar.make(requireView(), "Profile updated successfully", Snackbar.LENGTH_SHORT).show()
        findNavController().popBackStack()
    }

    private fun setLoading(isLoading: Boolean) {
        progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
        btnSaveProfile.isEnabled = !isLoading
        btnChangeImage.isEnabled = !isLoading
    }
}