package com.pawfinder.app.ui.post

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.pawfinder.app.R
import com.pawfinder.app.data.local.DatabaseProvider
import com.pawfinder.app.data.repository.PostRepository
import com.pawfinder.app.model.Post
import com.pawfinder.app.utils.CloudinaryManager
import java.util.UUID

class CreatePostFragment : Fragment(R.layout.fragment_create_post) {

    private lateinit var postViewModel: PostViewModel
    private lateinit var auth: FirebaseAuth

    private lateinit var rvSelectedImages: RecyclerView
    private lateinit var tvImageError: TextView
    private lateinit var progressIndicator: CircularProgressIndicator

    private lateinit var tilPetName: TextInputLayout
    private lateinit var tilPetType: TextInputLayout
    private lateinit var tilStatus: TextInputLayout
    private lateinit var tilDescription: TextInputLayout
    private lateinit var tilLocation: TextInputLayout

    private lateinit var etPetName: TextInputEditText
    private lateinit var etPetType: TextInputEditText
    private lateinit var etStatus: TextInputEditText
    private lateinit var etDescription: TextInputEditText
    private lateinit var etLocation: TextInputEditText

    private lateinit var btnCreatePost: MaterialButton
    private lateinit var selectedImagesAdapter: SelectedImagesAdapter

    private val selectedImageUris = mutableListOf<Uri>()
    private val uploadedImageUrls = mutableListOf<String>()

    private val pickMultipleImagesLauncher = registerForActivityResult(
        ActivityResultContracts.PickMultipleVisualMedia(5)
    ) { uris ->
        if (uris.isNotEmpty()) {
            val availableSlots = 5 - selectedImageUris.size
            val newUris = uris
                .filter { it !in selectedImageUris }
                .take(availableSlots)

            selectedImageUris.addAll(newUris)
            tvImageError.visibility = View.GONE
            selectedImagesAdapter.submitList(selectedImageUris.toList())

            if (uris.size > availableSlots) {
                Toast.makeText(
                    requireContext(),
                    "You can select up to 5 images only",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()
        initViewModel()
        initViews(view)
        setupRecyclerView()
        setupClickListeners()
    }

    private fun initViewModel() {
        val database = DatabaseProvider.getDatabase(requireContext())
        val repository = PostRepository(database.postDao())
        val factory = PostViewModelFactory(repository)
        postViewModel = ViewModelProvider(this, factory)[PostViewModel::class.java]
    }

    private fun initViews(view: View) {
        rvSelectedImages = view.findViewById(R.id.rvSelectedImages)
        tvImageError = view.findViewById(R.id.tvImageError)
        progressIndicator = view.findViewById(R.id.progressIndicator)

        tilPetName = view.findViewById(R.id.tilPetName)
        tilPetType = view.findViewById(R.id.tilPetType)
        tilStatus = view.findViewById(R.id.tilStatus)
        tilDescription = view.findViewById(R.id.tilDescription)
        tilLocation = view.findViewById(R.id.tilLocation)

        etPetName = view.findViewById(R.id.etPetName)
        etPetType = view.findViewById(R.id.etPetType)
        etStatus = view.findViewById(R.id.etStatus)
        etDescription = view.findViewById(R.id.etDescription)
        etLocation = view.findViewById(R.id.etLocation)

        btnCreatePost = view.findViewById(R.id.btnCreatePost)
    }

    private fun setupRecyclerView() {
        selectedImagesAdapter = SelectedImagesAdapter(
            maxImages = 5,
            onRemoveClick = { uri -> removeSelectedImage(uri) },
            onAddClick = { openImagePicker() }
        )

        rvSelectedImages.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        rvSelectedImages.adapter = selectedImagesAdapter
        selectedImagesAdapter.submitList(selectedImageUris.toList())
    }

    private fun setupClickListeners() {
        btnCreatePost.setOnClickListener {
            handleCreatePost()
        }
    }

    private fun openImagePicker() {
        if (selectedImageUris.size >= 5) {
            Toast.makeText(requireContext(), "Maximum 5 images allowed", Toast.LENGTH_SHORT).show()
            return
        }
        pickMultipleImagesLauncher.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        )
    }

    private fun removeSelectedImage(uri: Uri) {
        selectedImageUris.remove(uri)
        selectedImagesAdapter.submitList(selectedImageUris.toList())
        if (selectedImageUris.isEmpty()) {
            tvImageError.visibility = View.VISIBLE
        }
    }

    private fun handleCreatePost() {
        clearErrors()

        val petName = etPetName.text?.toString()?.trim().orEmpty()
        val petType = etPetType.text?.toString()?.trim().orEmpty()
        val status = etStatus.text?.toString()?.trim().orEmpty()
        val description = etDescription.text?.toString()?.trim().orEmpty()
        val location = etLocation.text?.toString()?.trim().orEmpty()

        if (!validateInput(petName, petType, status, description, location)) return

        setLoading(true)

        if (selectedImageUris.isEmpty()) {
            savePost(petName, petType, status, description, location, "")
        } else {
            uploadImagesAndSavePost(petName, petType, status, description, location)
        }
    }

    private fun uploadImagesAndSavePost(
        petName: String,
        petType: String,
        status: String,
        description: String,
        location: String
    ) {
        uploadedImageUrls.clear()
        var uploadedCount = 0

        selectedImageUris.forEach { uri ->
            CloudinaryManager.uploadPostImage(
                context = requireContext(),
                imageUri = uri,
                onSuccess = { url ->
                    uploadedImageUrls.add(url)
                    uploadedCount++
                    if (uploadedCount == selectedImageUris.size) {
                        val imageUrlString = uploadedImageUrls.joinToString(",")
                        savePost(petName, petType, status, description, location, imageUrlString)
                    }
                },
                onError = { error ->
                    setLoading(false)
                    Toast.makeText(requireContext(), "Image upload failed: $error", Toast.LENGTH_LONG).show()
                }
            )
        }
    }

    private fun savePost(
        petName: String,
        petType: String,
        status: String,
        description: String,
        location: String,
        imageUrl: String
    ) {
        val currentUser = auth.currentUser
        val userId = currentUser?.uid ?: "unknown_user"
        val userName = currentUser?.displayName ?: "PawFinder User"

        val post = Post(
            id = UUID.randomUUID().toString(),
            userId = userId,
            userName = userName,
            userImageUrl = "",
            petName = petName,
            petType = petType,
            status = status,
            description = description,
            imageUrl = imageUrl,
            location = location,
            timestamp = System.currentTimeMillis()
        )

        postViewModel.insertPost(post)
        setLoading(false)
        Toast.makeText(requireContext(), "Post created successfully", Toast.LENGTH_SHORT).show()
        clearForm()
    }

    private fun validateInput(
        petName: String,
        petType: String,
        status: String,
        description: String,
        location: String
    ): Boolean {
        var isValid = true

        if (petName.isBlank()) {
            tilPetName.error = "Pet name is required"
            isValid = false
        }
        if (petType.isBlank()) {
            tilPetType.error = "Pet type is required"
            isValid = false
        }
        if (status.isBlank()) {
            tilStatus.error = "Status is required"
            isValid = false
        }
        if (description.isBlank()) {
            tilDescription.error = "Description is required"
            isValid = false
        }
        if (location.isBlank()) {
            tilLocation.error = "Location is required"
            isValid = false
        }

        return isValid
    }

    private fun setLoading(isLoading: Boolean) {
        progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
        btnCreatePost.isEnabled = !isLoading
    }

    private fun clearErrors() {
        tvImageError.visibility = View.GONE
        tilPetName.error = null
        tilPetType.error = null
        tilStatus.error = null
        tilDescription.error = null
        tilLocation.error = null
    }

    private fun clearForm() {
        selectedImageUris.clear()
        uploadedImageUrls.clear()
        selectedImagesAdapter.submitList(emptyList())
        etPetName.text?.clear()
        etPetType.text?.clear()
        etStatus.text?.clear()
        etDescription.text?.clear()
        etLocation.text?.clear()
    }
}