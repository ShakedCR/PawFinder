package com.pawfinder.app.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pawfinder.app.data.repository.UserRepository
import com.pawfinder.app.model.User
import kotlinx.coroutines.launch

class UserViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> = _user

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun loadUserById(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _user.value = userRepository.getUserById(userId)
            _isLoading.value = false
        }
    }

    fun insertUser(user: User) {
        viewModelScope.launch {
            userRepository.insertUser(user)
            _user.value = user
        }
    }

    fun deleteUserById(userId: String) {
        viewModelScope.launch {
            userRepository.deleteUserById(userId)
            _user.value = null
        }
    }
}