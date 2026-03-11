package com.pawfinder.app.data.repository

import com.pawfinder.app.data.local.dao.PostDao
import com.pawfinder.app.data.local.mappers.toPost
import com.pawfinder.app.data.local.mappers.toPostEntity
import com.pawfinder.app.model.Post

class PostRepository(
    private val postDao: PostDao
) {

    suspend fun getAllPosts(): List<Post> {
        return postDao.getAllPosts().map { it.toPost() }
    }

    suspend fun getPostsByUserId(userId: String): List<Post> {
        return postDao.getPostsByUserId(userId).map { it.toPost() }
    }

    suspend fun getPostById(postId: String): Post? {
        return postDao.getPostById(postId)?.toPost()
    }

    suspend fun insertPost(post: Post) {
        postDao.insertPost(post.toPostEntity())
    }

    suspend fun insertPosts(posts: List<Post>) {
        postDao.insertPosts(posts.map { it.toPostEntity() })
    }

    suspend fun deletePostById(postId: String) {
        postDao.deletePostById(postId)
    }

    suspend fun deleteAllPosts() {
        postDao.deleteAllPosts()
    }
}