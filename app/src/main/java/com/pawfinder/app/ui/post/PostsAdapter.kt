package com.pawfinder.app.ui.post

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pawfinder.app.R
import com.pawfinder.app.model.Post

class PostsAdapter : RecyclerView.Adapter<PostsAdapter.PostViewHolder>() {

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
        holder.bind(posts[position])
    }

    override fun getItemCount(): Int = posts.size

    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val tvPetName: TextView = itemView.findViewById(R.id.tvPetName)
        private val tvPetType: TextView = itemView.findViewById(R.id.tvPetType)
        private val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)
        private val tvLocation: TextView = itemView.findViewById(R.id.tvLocation)
        private val tvDescription: TextView = itemView.findViewById(R.id.tvDescription)

        fun bind(post: Post) {
            tvPetName.text = post.petName
            tvPetType.text = "Type: ${post.petType}"
            tvStatus.text = "Status: ${post.status}"
            tvLocation.text = "Location: ${post.location}"
            tvDescription.text = post.description
        }
    }
}