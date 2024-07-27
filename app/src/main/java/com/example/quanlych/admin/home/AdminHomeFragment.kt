package com.example.quanlych.admin.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.quanlych.R
import com.example.quanlych.databinding.FragmentAdminHomeBinding

class AdminHomeFragment : Fragment() {

    private var _binding: FragmentAdminHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val adminHomeViewModel = ViewModelProvider(this).get(AdminHomeViewModel::class.java)

        _binding = FragmentAdminHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textAdminhome
        adminHomeViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        // Retrieve the email from SharedPreferences and set it to the TextView
        val sharedPref = requireActivity().getSharedPreferences("UserPref", Context.MODE_PRIVATE)
        val email = sharedPref.getString("email", "email@example.com")
        val emailTextView: TextView = binding.amazonDesc
        emailTextView.text = email

        // Set up click listeners for navigation
        binding.chartImage.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_chart)
        }
        binding.orderImage.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_order)
        }
        binding.userImage.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_user)
        }
        binding.productImage.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_product)
        }
        binding.categoryImage.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_category)
        }

        // Set up click listener for logout
        val logoutButton: Button = binding.btnLogout
        logoutButton.setOnClickListener {
            // Clear SharedPreferences or handle logout logic
            val sharedPref = requireActivity().getSharedPreferences("UserPref", Context.MODE_PRIVATE)
            with(sharedPref.edit()) {
                clear()
                apply()
            }
            // Navigate to login screen
            findNavController().navigate(R.id.action_adminhome_to_login)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
