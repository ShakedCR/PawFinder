package com.pawfinder.app.ui.feed

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.ChipGroup
import com.pawfinder.app.R
import com.pawfinder.app.data.local.DatabaseProvider
import com.pawfinder.app.data.repository.PostRepository
import com.pawfinder.app.model.Post
import com.pawfinder.app.ui.post.PostViewModel
import com.pawfinder.app.ui.post.PostViewModelFactory
import com.pawfinder.app.ui.post.PostsAdapter

class FeedFragment : Fragment(R.layout.fragment_feed) {

    private lateinit var postViewModel: PostViewModel
    private lateinit var feedAdapter: PostsAdapter

    private lateinit var rvFeed: RecyclerView
    private lateinit var searchView: SearchView
    private lateinit var chipGroupFilter: ChipGroup
    private lateinit var tvEmptyState: TextView

    private var allPosts = listOf<Post>()
    private var currentFilter = "All"
    private var currentQuery = ""

    private var isLoading = false
    private var currentPage = 0
    private val pageSize = 10

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewModel()
        initViews(view)
        setupRecyclerView()
        setupSearch()
        setupFilters()
        observeViewModel()
        loadPosts()
    }

    private fun initViewModel() {
        val database = DatabaseProvider.getDatabase(requireContext())
        val repository = PostRepository(database.postDao())
        val factory = PostViewModelFactory(repository)
        postViewModel = ViewModelProvider(this, factory)[PostViewModel::class.java]
    }

    private fun initViews(view: View) {
        rvFeed = view.findViewById(R.id.rvFeed)
        searchView = view.findViewById(R.id.searchView)
        chipGroupFilter = view.findViewById(R.id.chipGroupFilter)
        tvEmptyState = view.findViewById(R.id.tvEmptyState)
    }

    private fun setupRecyclerView() {
        feedAdapter = PostsAdapter(
            showActions = false,
            onPostClick = { post ->
                val action = FeedFragmentDirections
                    .actionFeedFragmentToPostDetailsFragment(post.id)
                findNavController().navigate(action)
            }
        )

        val layoutManager = LinearLayoutManager(requireContext())
        rvFeed.layoutManager = layoutManager
        rvFeed.adapter = feedAdapter

        rvFeed.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItem = layoutManager.findFirstVisibleItemPosition()
                if (!isLoading && (visibleItemCount + firstVisibleItem) >= totalItemCount - 2) {
                    loadMorePosts()
                }
            }
        })
    }

    private fun setupSearch() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                currentQuery = query?.trim() ?: ""
                applyFilters()
                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                currentQuery = newText?.trim() ?: ""
                applyFilters()
                return true
            }
        })
    }

    private fun setupFilters() {
        chipGroupFilter.setOnCheckedStateChangeListener { group, _ ->
            currentFilter = when (group.checkedChipId) {
                R.id.chipLost -> "Lost"
                R.id.chipFound -> "Found"
                R.id.chipDogs -> "Dogs"
                R.id.chipCats -> "Cats"
                else -> "All"
            }
            applyFilters()
        }
    }

    private fun observeViewModel() {
        postViewModel.posts.observe(viewLifecycleOwner) { posts ->
            allPosts = posts ?: emptyList()
            isLoading = false
            applyFilters()
        }
    }

    private fun loadPosts() {
        currentPage = 0
        isLoading = true
        postViewModel.loadAllPosts()
    }

    private fun loadMorePosts() {
        if (isLoading) return
        currentPage++
        isLoading = true
        postViewModel.loadAllPosts()
    }

    private fun applyFilters() {
        var filtered = allPosts

        if (currentQuery.isNotBlank()) {
            filtered = filtered.filter { post ->
                post.petName.contains(currentQuery, ignoreCase = true) ||
                        post.description.contains(currentQuery, ignoreCase = true) ||
                        post.location.contains(currentQuery, ignoreCase = true)
            }
        }

        filtered = when (currentFilter) {
            "Lost" -> filtered.filter { it.status.lowercase() == "lost" }
            "Found" -> filtered.filter { it.status.lowercase() == "found" }
            "Dogs" -> filtered.filter { it.petType.lowercase() == "dog" }
            "Cats" -> filtered.filter { it.petType.lowercase() == "cat" }
            else -> filtered
        }

        val paginated = filtered.take((currentPage + 1) * pageSize)
        feedAdapter.submitList(paginated)
        tvEmptyState.visibility = if (paginated.isEmpty()) View.VISIBLE else View.GONE
        rvFeed.visibility = if (paginated.isEmpty()) View.GONE else View.VISIBLE
    }
}