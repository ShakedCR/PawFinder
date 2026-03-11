package com.pawfinder.app.ui.post

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pawfinder.app.data.repository.PostRepository
import com.pawfinder.app.model.Post
import kotlinx.coroutines.launch

class PostViewModel(
    private val postRepository: PostRepository
) : ViewModel() {

    private val _posts = MutableLiveData<List<Post>>()
    val posts: LiveData<List<Post>> = _posts

    private val _selectedPost = MutableLiveData<Post?>()
    val selectedPost: LiveData<Post?> = _selectedPost

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun loadAllPosts() {
        viewModelScope.launch {
            _isLoading.value = true
            _posts.value = postRepository.getAllPosts()
            _isLoading.value = false
        }
    }

    fun loadPostsByUserId(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _posts.value = postRepository.getPostsByUserId(userId)
            _isLoading.value = false
        }
    }

    fun loadPostById(postId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _selectedPost.value = postRepository.getPostById(postId)
            _isLoading.value = false
        }
    }

    fun insertPost(post: Post) {
        viewModelScope.launch {
            postRepository.insertPost(post)
            loadAllPosts()
        }
    }

    fun updatePost(post: Post) {
        viewModelScope.launch {
            postRepository.insertPost(post)
            _selectedPost.value = post
        }
    }

    fun deletePostById(postId: String) {
        viewModelScope.launch {
            postRepository.deletePostById(postId)
            loadAllPosts()
        }
    }
}