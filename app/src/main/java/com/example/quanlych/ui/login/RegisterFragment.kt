package com.example.quanlych.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.quanlych.R
import com.example.quanlych.data.UserRepository
import com.example.quanlych.databinding.FragmentRegisterBinding

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val registerViewModel = ViewModelProvider(this).get(RegisterViewModel::class.java)

        registerViewModel.text.observe(viewLifecycleOwner) { text ->
            binding.textRegister.text = text
        }

        // Set up click listener for signup button
        binding.registerButton.setOnClickListener {
            val name = binding.username.text.toString()
            val email = binding.email.text.toString()
            val password = binding.password.text.toString()

            // Validate input (optional, add your own validation logic here)
            if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                val userRepository = UserRepository(requireContext())
                userRepository.registerUser(name, email, password)

                // Show success message
                Toast.makeText(requireContext(), "Đăng ký thành công", Toast.LENGTH_SHORT).show()

                // Navigate to login page
                findNavController().navigate(R.id.action_register_to_login)
            } else {
                Toast.makeText(requireContext(), "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show()
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
