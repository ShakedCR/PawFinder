package com.pawfinder.app.data.repository

import com.pawfinder.app.data.local.dao.PostDao
import com.pawfinder.app.data.local.entities.PostEntity

class PostRepository(
    private val postDao: PostDao
) {

    suspend fun getAllPosts(): List<PostEntity> {
        return postDao.getAllPosts()
    }

    suspend fun getPostsByUserId(userId: String): List<PostEntity> {
        return postDao.getPostsByUserId(userId)
    }

    suspend fun getPostById(postId: String): PostEntity? {
        return postDao.getPostById(postId)
    }

    suspend fun insertPost(post: PostEntity) {
        postDao.insertPost(post)
    }

    suspend fun insertPosts(posts: List<PostEntity>) {
        postDao.insertPosts(posts)
    }

    suspend fun deletePostById(postId: String) {
        postDao.deletePostById(postId)
    }

    suspend fun deleteAllPosts() {
        postDao.deleteAllPosts()
    }
}