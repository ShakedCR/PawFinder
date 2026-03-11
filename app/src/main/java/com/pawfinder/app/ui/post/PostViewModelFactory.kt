package com.pawfinder.app.ui.post

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.pawfinder.app.data.repository.PostRepository

class PostViewModelFactory(
    private val postRepository: PostRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PostViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PostViewModel(postRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}