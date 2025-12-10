package com.example.blogapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.blogapp.databinding.ItemBlogBinding
import com.example.blogapp.data.model.Blog

class BlogAdapter(
    private val onLikeClick: (Blog) -> Unit,
    private val onSaveClick: (Blog) -> Unit
) : RecyclerView.Adapter<BlogAdapter.BlogViewHolder>() {

    private var blogs = listOf<Blog>()
    private var savedBlogIds = setOf<String>()

    inner class BlogViewHolder(private val binding: ItemBlogBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(blog: Blog) {
            binding.apply {
                tvBlogTitle.text = blog.title
                
                // Truncate content if too long
                val contentPreview = if (blog.content.length > 100) {
                    blog.content.substring(0, 100) + "..."
                } else {
                    blog.content
                }
                tvBlogContent.text = contentPreview
                
                tvLikes.text = "${blog.likes} likes"
                
                btnLike.setOnClickListener {
                    onLikeClick(blog)
                }
                
                val isSaved = savedBlogIds.contains(blog.id)
                btnSave.apply {
                    text = if (isSaved) "♥ Saved" else "♡ Save"
                    setOnClickListener {
                        onSaveClick(blog)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlogViewHolder {
        val binding = ItemBlogBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BlogViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BlogViewHolder, position: Int) {
        holder.bind(blogs[position])
    }

    override fun getItemCount() = blogs.size

    fun setBlogs(newBlogs: List<Blog>) {
        blogs = newBlogs
        notifyDataSetChanged()
    }

    fun setSavedBlogIds(ids: List<String>) {
        savedBlogIds = ids.toSet()
        notifyDataSetChanged()
    }
}
