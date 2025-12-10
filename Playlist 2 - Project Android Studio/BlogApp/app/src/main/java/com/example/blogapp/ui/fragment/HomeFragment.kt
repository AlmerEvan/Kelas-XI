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
import com.example.blogapp.databinding.FragmentHomeBinding
import com.example.blogapp.data.model.Blog
import com.example.blogapp.data.repository.Result
import com.example.blogapp.ui.adapter.BlogAdapter
import com.example.blogapp.ui.viewmodel.AuthViewModel
import com.example.blogapp.ui.viewmodel.BlogViewModel

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var blogViewModel: BlogViewModel
    private lateinit var authViewModel: AuthViewModel
    private lateinit var blogAdapter: BlogAdapter
    private var userId: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        blogViewModel = ViewModelProvider(this).get(BlogViewModel::class.java)
        authViewModel = ViewModelProvider(this).get(AuthViewModel::class.java)
        
        userId = authViewModel.getCurrentUserId() ?: ""
        
        setupRecyclerView()
        observeBlogs()
        observeError()
        
        binding.btnAddBlog.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_addBlogFragment)
        }

        binding.btnSaved.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_savedFragment)
        }

        binding.btnLogout.setOnClickListener {
            authViewModel.logout()
            requireActivity().finish()
        }
    }

    private fun setupRecyclerView() {
        blogAdapter = BlogAdapter(
            onLikeClick = { blog ->
                blogViewModel.updateLikes(blog.id, 1)
            },
            onSaveClick = { blog ->
                if (userId.isNotEmpty()) {
                    blogViewModel.saveBlogForUser(userId, blog.id)
                    Toast.makeText(context, "Blog saved", Toast.LENGTH_SHORT).show()
                }
            }
        )
        
        binding.recyclerViewBlogs.apply {
            adapter = blogAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun observeBlogs() {
        blogViewModel.blogs.observe(viewLifecycleOwner) { blogs ->
            blogAdapter.setBlogs(blogs)
        }

        if (userId.isNotEmpty()) {
            blogViewModel.fetchSavedBlogs(userId)
        }

        blogViewModel.savedBlogIds.observe(viewLifecycleOwner) { ids ->
            blogAdapter.setSavedBlogIds(ids)
        }
    }

    private fun observeError() {
        blogViewModel.error.observe(viewLifecycleOwner) { error ->
            if (!error.isNullOrEmpty()) {
                Toast.makeText(context, "Error: $error", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        blogViewModel.fetchBlogs()
        if (userId.isNotEmpty()) {
            blogViewModel.fetchSavedBlogs(userId)
        }
    }
}
