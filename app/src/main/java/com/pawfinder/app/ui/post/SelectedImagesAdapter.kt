package com.pawfinder.app.ui.post

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.pawfinder.app.R

class SelectedImagesAdapter(
    private val onRemoveClick: (Uri) -> Unit
) : RecyclerView.Adapter<SelectedImagesAdapter.SelectedImageViewHolder>() {

    private val imageUris = mutableListOf<Uri>()

    fun submitList(newUris: List<Uri>) {
        imageUris.clear()
        imageUris.addAll(newUris)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectedImageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_selected_image, parent, false)
        return SelectedImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: SelectedImageViewHolder, position: Int) {
        val uri = imageUris[position]
        holder.bind(uri)

        holder.btnRemoveImage.setOnClickListener {
            onRemoveClick(uri)
        }
    }

    override fun getItemCount(): Int = imageUris.size

    class SelectedImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.ivSelectedImage)
        val btnRemoveImage: ImageButton = itemView.findViewById(R.id.btnRemoveImage)

        fun bind(uri: Uri) {
            Glide.with(itemView.context)
                .load(uri)
                .centerCrop()
                .into(imageView)
        }
    }
}