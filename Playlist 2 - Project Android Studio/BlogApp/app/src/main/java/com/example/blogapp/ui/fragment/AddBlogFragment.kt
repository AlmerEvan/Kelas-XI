package com.example.blogapp.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.blogapp.R
import com.example.blogapp.databinding.FragmentAddBlogBinding
import com.example.blogapp.data.repository.Result
import com.example.blogapp.ui.viewmodel.AuthViewModel
import com.example.blogapp.ui.viewmodel.BlogViewModel

class AddBlogFragment : Fragment() {
    private lateinit var binding: FragmentAddBlogBinding
    private lateinit var blogViewModel: BlogViewModel
    private lateinit var authViewModel: AuthViewModel
    private var userId: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddBlogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        blogViewModel = ViewModelProvider(this).get(BlogViewModel::class.java)
        authViewModel = ViewModelProvider(this).get(AuthViewModel::class.java)
        
        userId = authViewModel.getCurrentUserId() ?: ""
        
        binding.btnPublish.setOnClickListener {
            publishBlog()
        }

        blogViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.btnPublish.isEnabled = !isLoading
        }
    }

    private fun publishBlog() {
        val title = binding.etTitle.text.toString().trim()
        val content = binding.etContent.text.toString().trim()
        
        if (title.isEmpty() || content.isEmpty()) {
            Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }
        
        if (userId.isEmpty()) {
            Toast.makeText(context, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }
        
        blogViewModel.saveBlog(title, content, userId)
        
        // Clear fields and navigate back
        binding.etTitle.text.clear()
        binding.etContent.text.clear()
        
        Toast.makeText(context, "Blog published successfully", Toast.LENGTH_SHORT).show()
        findNavController().navigate(R.id.action_addBlogFragment_to_homeFragment)
    }
}
