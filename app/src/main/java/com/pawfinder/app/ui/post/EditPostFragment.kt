package com.pawfinder.app.ui.post

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
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

class EditPostFragment : Fragment(R.layout.fragment_edit_post) {

    private lateinit var postViewModel: PostViewModel
    private lateinit var auth: FirebaseAuth

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

    private lateinit var ivEditPostImage: ImageView
    private lateinit var btnChangeImage: MaterialButton
    private lateinit var btnRemoveImage: MaterialButton
    private lateinit var btnSavePostChanges: MaterialButton
    private lateinit var progressIndicator: CircularProgressIndicator

    private var currentPost: Post? = null
    private var postId: String? = null
    private var selectedNewImageUri: Uri? = null
    private var removeCurrentImage = false

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            selectedNewImageUri = uri
            removeCurrentImage = false

            ivEditPostImage.visibility = View.VISIBLE
            Glide.with(this)
                .load(uri)
                .into(ivEditPostImage)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        postId = arguments?.getString("postId")

        initViewModel()
        initViews(view)
        setupClickListeners()
        observeViewModel()

        postId?.let { postViewModel.loadPostById(it) }
    }

    private fun initViewModel() {
        val database = DatabaseProvider.getDatabase(requireContext())
        val repository = PostRepository(database.postDao())
        val factory = PostViewModelFactory(repository)

        postViewModel = ViewModelProvider(this, factory)[PostViewModel::class.java]
    }

    private fun initViews(view: View) {
        tilPetName = view.findViewById(R.id.tilEditPetName)
        tilPetType = view.findViewById(R.id.tilEditPetType)
        tilStatus = view.findViewById(R.id.tilEditStatus)
        tilDescription = view.findViewById(R.id.tilEditDescription)
        tilLocation = view.findViewById(R.id.tilEditLocation)

        etPetName = view.findViewById(R.id.etEditPetName)
        etPetType = view.findViewById(R.id.etEditPetType)
        etStatus = view.findViewById(R.id.etEditStatus)
        etDescription = view.findViewById(R.id.etEditDescription)
        etLocation = view.findViewById(R.id.etEditLocation)

        ivEditPostImage = view.findViewById(R.id.ivEditPostImage)
        btnChangeImage = view.findViewById(R.id.btnChangeImage)
        btnRemoveImage = view.findViewById(R.id.btnRemoveImage)
        btnSavePostChanges = view.findViewById(R.id.btnSavePostChanges)
        progressIndicator = view.findViewById(R.id.progressIndicator)
    }

    private fun setupClickListeners() {
        btnChangeImage.setOnClickListener {
            pickImageLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        }

        btnRemoveImage.setOnClickListener {
            selectedNewImageUri = null
            removeCurrentImage = true
            ivEditPostImage.setImageDrawable(null)
            ivEditPostImage.visibility = View.GONE
        }

        btnSavePostChanges.setOnClickListener {
            handleSaveChanges()
        }
    }

    private fun observeViewModel() {
        postViewModel.selectedPost.observe(viewLifecycleOwner) { post ->
            post?.let {
                currentPost = it
                bindPostData(it)
            }
        }
    }

    private fun bindPostData(post: Post) {
        etPetName.setText(post.petName)
        etPetType.setText(post.petType)
        etStatus.setText(post.status)
        etDescription.setText(post.description)
        etLocation.setText(post.location)

        if (post.imageUrl.isNotBlank()) {
            ivEditPostImage.visibility = View.VISIBLE
            Glide.with(this)
                .load(post.imageUrl.split(",").first())
                .into(ivEditPostImage)
        } else {
            ivEditPostImage.visibility = View.GONE
        }
    }

    private fun handleSaveChanges() {
        clearErrors()

        val petName = etPetName.text?.toString()?.trim().orEmpty()
        val petType = etPetType.text?.toString()?.trim().orEmpty()
        val status = etStatus.text?.toString()?.trim().orEmpty()
        val description = etDescription.text?.toString()?.trim().orEmpty()
        val location = etLocation.text?.toString()?.trim().orEmpty()

        if (!validateInput(petName, petType, status, description, location)) {
            return
        }

        val existingPost = currentPost ?: return
        val currentUser = auth.currentUser

        setLoading(true)

        val newImageUri = selectedNewImageUri

        if (newImageUri != null) {
            CloudinaryManager.uploadPostImage(
                context = requireContext(),
                imageUri = newImageUri,
                onSuccess = { uploadedUrl ->
                    val updatedPost = existingPost.copy(
                        userId = currentUser?.uid ?: existingPost.userId,
                        userName = currentUser?.displayName ?: existingPost.userName,
                        petName = petName,
                        petType = petType,
                        status = status,
                        description = description,
                        location = location,
                        imageUrl = uploadedUrl
                    )

                    postViewModel.updatePost(updatedPost) {
                        setLoading(false)
                        Toast.makeText(
                            requireContext(),
                            "Post updated successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                        parentFragmentManager.popBackStack()
                    }
                },
                onError = { error ->
                    setLoading(false)
                    Toast.makeText(
                        requireContext(),
                        "Failed to upload image: $error",
                        Toast.LENGTH_LONG
                    ).show()
                }
            )
        } else {
            val finalImageUrl = if (removeCurrentImage) "" else existingPost.imageUrl

            val updatedPost = existingPost.copy(
                userId = currentUser?.uid ?: existingPost.userId,
                userName = currentUser?.displayName ?: existingPost.userName,
                petName = petName,
                petType = petType,
                status = status,
                description = description,
                location = location,
                imageUrl = finalImageUrl
            )

            postViewModel.updatePost(updatedPost) {
                setLoading(false)
                Toast.makeText(
                    requireContext(),
                    "Post updated successfully",
                    Toast.LENGTH_SHORT
                ).show()
                parentFragmentManager.popBackStack()
            }
        }
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

    private fun clearErrors() {
        tilPetName.error = null
        tilPetType.error = null
        tilStatus.error = null
        tilDescription.error = null
        tilLocation.error = null
    }

    private fun setLoading(isLoading: Boolean) {
        progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
        btnSavePostChanges.isEnabled = !isLoading
        btnChangeImage.isEnabled = !isLoading
        btnRemoveImage.isEnabled = !isLoading
    }
}