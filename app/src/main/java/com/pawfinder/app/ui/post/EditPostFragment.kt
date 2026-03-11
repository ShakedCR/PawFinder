package com.pawfinder.app.ui.post

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.pawfinder.app.R
import com.pawfinder.app.data.local.DatabaseProvider
import com.pawfinder.app.data.repository.PostRepository
import com.pawfinder.app.model.Post

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
    private lateinit var btnSavePostChanges: MaterialButton

    private var currentPost: Post? = null
    private var postId: String? = null

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

        btnSavePostChanges = view.findViewById(R.id.btnSavePostChanges)
    }

    private fun setupClickListeners() {
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

        val updatedPost = existingPost.copy(
            userId = currentUser?.uid ?: existingPost.userId,
            userName = currentUser?.displayName ?: existingPost.userName,
            petName = petName,
            petType = petType,
            status = status,
            description = description,
            location = location
        )

        postViewModel.updatePost(updatedPost)

        Toast.makeText(requireContext(), "Post updated successfully", Toast.LENGTH_SHORT).show()
        parentFragmentManager.popBackStack()
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
}