package com.pawfinder.app.ui.post

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.pawfinder.app.R

class SelectedImagesAdapter(
    private val maxImages: Int = 5,
    private val onRemoveClick: (Uri) -> Unit,
    private val onAddClick: () -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val imageUris = mutableListOf<Uri>()

    companion object {
        private const val VIEW_TYPE_IMAGE = 1
        private const val VIEW_TYPE_ADD = 2
    }

    fun submitList(newUris: List<Uri>) {
        imageUris.clear()
        imageUris.addAll(newUris)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return if (imageUris.size < maxImages) {
            imageUris.size + 1
        } else {
            imageUris.size
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position < imageUris.size) {
            VIEW_TYPE_IMAGE
        } else {
            VIEW_TYPE_ADD
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_IMAGE -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_selected_image, parent, false)
                SelectedImageViewHolder(view)
            }

            VIEW_TYPE_ADD -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_add_image, parent, false)
                AddImageViewHolder(view)
            }

            else -> {
                throw IllegalArgumentException("Unknown view type")
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is SelectedImageViewHolder -> {
                val uri = imageUris[position]
                holder.bind(uri)

                holder.btnRemoveImage.setOnClickListener {
                    onRemoveClick(uri)
                }
            }

            is AddImageViewHolder -> {
                holder.itemView.setOnClickListener {
                    onAddClick()
                }
            }
        }
    }

    class SelectedImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.ivSelectedImage)
        val btnRemoveImage: View = itemView.findViewById(R.id.btnRemoveImage)

        fun bind(uri: Uri) {
            Glide.with(itemView.context)
                .load(uri)
                .centerCrop()
                .into(imageView)
        }
    }

    class AddImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}