package com.pawfinder.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.pawfinder.app.data.local.dao.PostDao
import com.pawfinder.app.data.local.mappers.toPost
import com.pawfinder.app.data.local.mappers.toPostEntity
import com.pawfinder.app.model.Post
import kotlinx.coroutines.tasks.await

class PostRepository(
    private val postDao: PostDao
) {
    private val firestore = FirebaseFirestore.getInstance()
    private val postsCollection = firestore.collection("posts")

    suspend fun getAllPosts(): List<Post> {
        return try {
            val snapshot = postsCollection
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .await()

            val posts = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Post::class.java)?.copy(id = doc.id)
            }

            postDao.deleteAllPosts()
            postDao.insertPosts(posts.map { it.toPostEntity() })

            posts
        } catch (e: Exception) {
            postDao.getAllPosts().map { it.toPost() }
        }
    }

    suspend fun getPostsByUserId(userId: String): List<Post> {
        return try {
            val snapshot = postsCollection
                .whereEqualTo("userId", userId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .await()

            val posts = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Post::class.java)?.copy(id = doc.id)
            }

            postDao.insertPosts(posts.map { it.toPostEntity() })
            posts
        } catch (e: Exception) {
            postDao.getPostsByUserId(userId).map { it.toPost() }
        }
    }

    suspend fun getPostById(postId: String): Post? {
        return try {
            val doc = postsCollection.document(postId).get().await()
            doc.toObject(Post::class.java)?.copy(id = doc.id)
        } catch (e: Exception) {
            postDao.getPostById(postId)?.toPost()
        }
    }

    suspend fun insertPost(post: Post) {
        val docRef = postsCollection.document(post.id)
        docRef.set(post).await()
        postDao.insertPost(post.toPostEntity())
    }

    suspend fun deletePostById(postId: String) {
        postsCollection.document(postId).delete().await()
        postDao.deletePostById(postId)
    }

    suspend fun deleteAllPosts() {
        postDao.deleteAllPosts()
    }
}