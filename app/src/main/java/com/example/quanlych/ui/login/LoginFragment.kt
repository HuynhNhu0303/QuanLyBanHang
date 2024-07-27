package com.example.quanlych.ui.login

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.quanlych.MainActivity
import com.example.quanlych.R
import com.example.quanlych.data.UserRepository
import com.example.quanlych.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Set up click listener for login button
        binding.loginButton.setOnClickListener {
            val email = binding.username.text.toString()
            val password = binding.password.text.toString()
            val userRepository = UserRepository(requireContext())
            val (isLoginSuccessful, role) = userRepository.loginUser(email, password)

            if (isLoginSuccessful) {
                // Save user information to SharedPreferences
                val sharedPref = requireActivity().getSharedPreferences("UserPref", Context.MODE_PRIVATE)
                with(sharedPref.edit()) {
                    putString("username", email) // Assuming email as username
                    putString("email", email)
                    apply()
                }

                // Update the navigation header with the new user's info
                (activity as MainActivity).updateNavHeader()

                // Navigate based on user role
                if (role == "quản lý") {
                    findNavController().navigate(R.id.action_login_to_admin_home)
                } else {
                    findNavController().navigate(R.id.action_login_to_home)
                }
            } else {
                // Show error message
                Toast.makeText(requireContext(), "Email hoặc mật khẩu không đúng", Toast.LENGTH_SHORT).show()
            }
        }

        // Set up click listener for signup TextView
        binding.signupText.setOnClickListener {
            // Navigate to the Register Fragment
            findNavController().navigate(R.id.action_login_to_register)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}