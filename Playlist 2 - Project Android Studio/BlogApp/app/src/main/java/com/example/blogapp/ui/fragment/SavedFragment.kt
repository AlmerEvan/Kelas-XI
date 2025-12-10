package com.example.blogapp.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.blogapp.R
import com.example.blogapp.databinding.FragmentSavedBinding
import com.example.blogapp.data.model.Blog
import com.example.blogapp.data.repository.Result
import com.example.blogapp.ui.adapter.BlogAdapter
import com.example.blogapp.ui.viewmodel.AuthViewModel
import com.example.blogapp.ui.viewmodel.BlogViewModel

class SavedFragment : Fragment() {
    private lateinit var binding: FragmentSavedBinding
    private lateinit var blogViewModel: BlogViewModel
    private lateinit var authViewModel: AuthViewModel
    private lateinit var blogAdapter: BlogAdapter
    private var userId: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSavedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        blogViewModel = ViewModelProvider(this).get(BlogViewModel::class.java)
        authViewModel = ViewModelProvider(this).get(AuthViewModel::class.java)
        
        userId = authViewModel.getCurrentUserId() ?: ""
        
        setupRecyclerView()
        observeSavedBlogs()
        
        binding.btnBack.setOnClickListener {
            findNavController().navigate(R.id.action_savedFragment_to_homeFragment)
        }
    }

    private fun setupRecyclerView() {
        blogAdapter = BlogAdapter(
            onLikeClick = { blog ->
                blogViewModel.updateLikes(blog.id, 1)
            },
            onSaveClick = { blog ->
                if (userId.isNotEmpty()) {
                    blogViewModel.removeSavedBlog(userId, blog.id)
                    Toast.makeText(context, "Blog removed from saved", Toast.LENGTH_SHORT).show()
                }
            }
        )
        
        binding.recyclerViewSavedBlogs.apply {
            adapter = blogAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun observeSavedBlogs() {
        if (userId.isNotEmpty()) {
            blogViewModel.getSavedBlog(userId).observe(viewLifecycleOwner) { result ->
                when (result) {
                    is Result.Success -> {
                        blogAdapter.setBlogs(result.data)
                        binding.progressBar.visibility = View.GONE
                        if (result.data.isEmpty()) {
                            binding.tvNoSavedBlogs.visibility = View.VISIBLE
                        } else {
                            binding.tvNoSavedBlogs.visibility = View.GONE
                        }
                    }
                    is Result.Error -> {
                        Toast.makeText(context, "Error: ${result.exception.message}", Toast.LENGTH_SHORT).show()
                        binding.progressBar.visibility = View.GONE
                    }
                    is Result.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        observeSavedBlogs()
    }
}
