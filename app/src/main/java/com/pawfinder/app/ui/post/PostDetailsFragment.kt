package com.pawfinder.app.ui.post

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.chip.Chip
import com.pawfinder.app.R
import com.pawfinder.app.data.local.DatabaseProvider
import com.pawfinder.app.data.repository.PostRepository

class PostDetailsFragment : Fragment(R.layout.fragment_post_details) {

    private lateinit var postViewModel: PostViewModel

    private lateinit var viewPagerImages: ViewPager2
    private lateinit var dotsIndicator: LinearLayout
    private lateinit var tvDetailPetName: TextView
    private lateinit var chipDetailStatus: Chip
    private lateinit var tvDetailPetType: TextView
    private lateinit var tvDetailLocation: TextView
    private lateinit var tvDetailUserName: TextView
    private lateinit var tvDetailDescription: TextView
    private lateinit var cardBreedInfo: CardView
    private lateinit var tvBreedInfo: TextView

    private var postId: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postId = arguments?.getString("postId")
        initViewModel()
        initViews(view)
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
        viewPagerImages = view.findViewById(R.id.viewPagerImages)
        dotsIndicator = view.findViewById(R.id.dotsIndicator)
        tvDetailPetName = view.findViewById(R.id.tvDetailPetName)
        chipDetailStatus = view.findViewById(R.id.chipDetailStatus)
        tvDetailPetType = view.findViewById(R.id.tvDetailPetType)
        tvDetailLocation = view.findViewById(R.id.tvDetailLocation)
        tvDetailUserName = view.findViewById(R.id.tvDetailUserName)
        tvDetailDescription = view.findViewById(R.id.tvDetailDescription)
        cardBreedInfo = view.findViewById(R.id.cardBreedInfo)
        tvBreedInfo = view.findViewById(R.id.tvBreedInfo)
    }

    private fun setupDots(count: Int) {
        dotsIndicator.removeAllViews()
        val dots = Array(count) { ImageView(requireContext()) }

        dots.forEachIndexed { index, dot ->
            dot.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    if (index == 0) R.drawable.tab_indicator_dot_selected
                    else R.drawable.tab_indicator_dot
                )
            )
            val params = LinearLayout.LayoutParams(20, 20)
            params.setMargins(8, 0, 8, 0)
            dot.layoutParams = params
            dotsIndicator.addView(dot)
        }

        viewPagerImages.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                dots.forEachIndexed { index, dot ->
                    dot.setImageDrawable(
                        ContextCompat.getDrawable(
                            requireContext(),
                            if (index == position) R.drawable.tab_indicator_dot_selected
                            else R.drawable.tab_indicator_dot
                        )
                    )
                }
            }
        })
    }

    private fun observeViewModel() {
        postViewModel.selectedPost.observe(viewLifecycleOwner) { post ->
            post?.let {
                tvDetailPetName.text = it.petName
                tvDetailPetType.text = "Type: ${it.petType}"
                tvDetailLocation.text = it.location
                tvDetailUserName.text = "Posted by: ${it.userName}"
                tvDetailDescription.text = it.description

                chipDetailStatus.text = it.status
                if (it.status.lowercase() == "lost") {
                    chipDetailStatus.setChipBackgroundColorResource(R.color.lost_red)
                } else {
                    chipDetailStatus.setChipBackgroundColorResource(R.color.found_green)
                }

                if (it.imageUrl.isNotBlank()) {
                    val images = it.imageUrl.split(",").filter { url -> url.isNotBlank() }
                    if (images.isNotEmpty()) {
                        viewPagerImages.visibility = View.VISIBLE
                        val imagesAdapter = PostImagesAdapter(images)
                        viewPagerImages.adapter = imagesAdapter

                        if (images.size > 1) {
                            dotsIndicator.visibility = View.VISIBLE
                            setupDots(images.size)
                        }
                    }
                }
            }
        }
    }
}