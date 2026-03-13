package com.pawfinder.app.ui.post

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.pawfinder.app.R
import com.pawfinder.app.model.Post

class PostsAdapter(
    private val onEditClick: (Post) -> Unit,
    private val onDeleteClick: (Post) -> Unit
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
        holder.bind(post)

        holder.btnPostActions.setOnClickListener { anchorView ->
            showPopupMenu(anchorView, post)
        }
    }

    override fun getItemCount(): Int = posts.size

    private fun showPopupMenu(anchorView: View, post: Post) {
        val popupMenu = PopupMenu(anchorView.context, anchorView)
        popupMenu.menu.add(0, 1, 0, "Edit Post")
        popupMenu.menu.add(0, 2, 1, "Delete Post")

        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                1 -> {
                    onEditClick(post)
                    true
                }

                2 -> {
                    onDeleteClick(post)
                    true
                }

                else -> false
            }
        }

        popupMenu.show()
    }

    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val tvPetName: TextView = itemView.findViewById(R.id.tvPetName)
        private val tvPetType: TextView = itemView.findViewById(R.id.tvPetType)
        private val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)
        private val tvLocation: TextView = itemView.findViewById(R.id.tvLocation)
        private val tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
        private val ivPostImage: ImageView = itemView.findViewById(R.id.ivPostImage)
        val btnPostActions: ImageButton = itemView.findViewById(R.id.btnPostActions)

        fun bind(post: Post) {
            tvPetName.text = post.petName
            tvPetType.text = "Type: ${post.petType}"
            tvStatus.text = "Status: ${post.status}"
            tvLocation.text = "Location: ${post.location}"
            tvDescription.text = post.description

            if (post.imageUrl.isNotBlank()) {
                ivPostImage.visibility = View.VISIBLE

                Glide.with(itemView.context)
                    .load(post.imageUrl)
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)
                    .into(ivPostImage)
            } else {
                ivPostImage.visibility = View.GONE
            }
        }
    }
}