package com.pawfinder.app.ui.post

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.pawfinder.app.R
import com.pawfinder.app.model.Post

class PostsAdapter(
    private val onEditClick: (Post) -> Unit = {},
    private val onDeleteClick: (Post) -> Unit = {},
    private val onPostClick: (Post) -> Unit = {},
    private val showActions: Boolean = true
) : RecyclerView.Adapter<PostsAdapter.PostViewHolder>() {

    private val posts = mutableListOf<Post>()

    fun submitList(newPosts: List<Post>) {
        posts.clear()
        posts.addAll(newPosts)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_post, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]
        holder.bind(post, showActions)

        holder.itemView.setOnClickListener {
            onPostClick(post)
        }

        if (showActions) {
            holder.btnPostActions.setOnClickListener { anchorView ->
                MaterialAlertDialogBuilder(anchorView.context)
                    .setTitle(post.petName)
                    .setItems(arrayOf("✏️ Edit Post", "🗑️ Delete Post")) { _, which ->
                        when (which) {
                            0 -> onEditClick(post)
                            1 -> onDeleteClick(post)
                        }
                    }
                    .show()
            }
        }
    }

    override fun getItemCount(): Int = posts.size

    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val tvPetName: TextView = itemView.findViewById(R.id.tvPetName)
        private val tvPetType: TextView = itemView.findViewById(R.id.tvPetType)
        private val chipStatus: Chip = itemView.findViewById(R.id.chipStatus)
        private val tvLocation: TextView = itemView.findViewById(R.id.tvLocation)
        private val tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
        private val ivPostImage: ImageView = itemView.findViewById(R.id.ivPostImage)
        val btnPostActions: ImageButton = itemView.findViewById(R.id.btnPostActions)

        fun bind(post: Post, showActions: Boolean) {
            tvPetName.text = post.petName
            tvPetType.text = "Type: ${post.petType}"
            tvLocation.text = "Location: ${post.location}"
            tvDescription.text = post.description

            btnPostActions.visibility = if (showActions) View.VISIBLE else View.GONE

            chipStatus.text = post.status
            if (post.status.lowercase() == "lost") {
                chipStatus.setChipBackgroundColorResource(R.color.lost_red)
            } else {
                chipStatus.setChipBackgroundColorResource(R.color.found_green)
            }

            if (post.imageUrl.isNotBlank()) {
                ivPostImage.visibility = View.VISIBLE
                Glide.with(itemView.context)
                    .load(post.imageUrl.split(",").first())
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)
                    .into(ivPostImage)
            } else {
                ivPostImage.visibility = View.GONE
            }
        }
    }
}