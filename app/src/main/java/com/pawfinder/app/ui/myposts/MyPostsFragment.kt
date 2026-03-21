package com.pawfinder.app.ui.myposts

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.pawfinder.app.R
import com.pawfinder.app.data.local.DatabaseProvider
import com.pawfinder.app.data.repository.PostRepository
import com.pawfinder.app.model.Post
import com.pawfinder.app.ui.post.PostViewModel
import com.pawfinder.app.ui.post.PostViewModelFactory
import com.pawfinder.app.ui.post.PostsAdapter

class MyPostsFragment : Fragment(R.layout.fragment_my_posts) {

    private lateinit var auth: FirebaseAuth
    private lateinit var postViewModel: PostViewModel
    private lateinit var postsAdapter: PostsAdapter

    private lateinit var rvMyPosts: RecyclerView
    private lateinit var tvEmptyState: View

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()
        initViewModel()
        initViews(view)
        setupRecyclerView()
        observeViewModel()
        loadCurrentUserPosts()
    }

    override fun onResume() {
        super.onResume()
        loadCurrentUserPosts()
    }

    private fun initViewModel() {
        val database = DatabaseProvider.getDatabase(requireContext())
        val repository = PostRepository(database.postDao())
        val factory = PostViewModelFactory(repository)
        postViewModel = ViewModelProvider(this, factory)[PostViewModel::class.java]
    }

    private fun initViews(view: View) {
        rvMyPosts = view.findViewById(R.id.rvMyPosts)
        tvEmptyState = view.findViewById(R.id.tvEmptyState)
    }

    private fun setupRecyclerView() {
        postsAdapter = PostsAdapter(
            onEditClick = { post -> handleEditPost(post) },
            onDeleteClick = { post -> showDeleteConfirmationDialog(post) },
            onPostClick = { post ->
                val bundle = Bundle().apply { putString("postId", post.id) }
                findNavController().navigate(R.id.action_myPostsFragment_to_postDetailsFragment, bundle)
            }
        )
        rvMyPosts.layoutManager = LinearLayoutManager(requireContext())
        rvMyPosts.adapter = postsAdapter
    }

    private fun observeViewModel() {
        postViewModel.posts.observe(viewLifecycleOwner) { posts ->
            postsAdapter.submitList(posts)
            if (posts.isNullOrEmpty()) {
                tvEmptyState.visibility = View.VISIBLE
                rvMyPosts.visibility = View.GONE
            } else {
                tvEmptyState.visibility = View.GONE
                rvMyPosts.visibility = View.VISIBLE
            }
        }
    }

    private fun loadCurrentUserPosts() {
        val currentUserId = auth.currentUser?.uid ?: return
        postViewModel.loadPostsByUserId(currentUserId)
    }

    private fun handleEditPost(post: Post) {
        val bundle = Bundle().apply { putString("postId", post.id) }
        findNavController().navigate(R.id.action_myPostsFragment_to_editPostFragment, bundle)
    }

    private fun showDeleteConfirmationDialog(post: Post) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Post")
            .setMessage("Are you sure you want to delete this post?")
            .setPositiveButton("Delete") { _, _ -> deletePost(post) }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deletePost(post: Post) {
        postViewModel.deletePostById(post.id)
        loadCurrentUserPosts()
        Toast.makeText(requireContext(), "Post deleted successfully", Toast.LENGTH_SHORT).show()
    }
}