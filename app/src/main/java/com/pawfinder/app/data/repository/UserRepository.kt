package com.pawfinder.app.data.repository

import com.pawfinder.app.data.local.dao.UserDao
import com.pawfinder.app.data.local.entities.UserEntity

class UserRepository(
    private val userDao: UserDao
) {

    suspend fun getUserById(userId: String): UserEntity? {
        return userDao.getUserById(userId)
    }

    suspend fun insertUser(user: UserEntity) {
        userDao.insertUser(user)
    }

    suspend fun deleteUserById(userId: String) {
        userDao.deleteUserById(userId)
    }

    suspend fun deleteAllUsers() {
        userDao.deleteAllUsers()
    }
}