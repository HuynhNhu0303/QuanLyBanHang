package com.example.quanlych.admin.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.quanlych.R
import com.example.quanlych.data.UserRepository
import com.example.quanlych.databinding.FragmentAddUserBinding

class AddUserFragment : Fragment() {

    private var _binding: FragmentAddUserBinding? = null
    private val binding get() = _binding!!
    private lateinit var userRepository: UserRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddUserBinding.inflate(inflater, container, false)
        val root: View = binding.root

        userRepository = UserRepository(requireContext())

        val saveButton: Button = binding.buttonSave
        saveButton.setOnClickListener {
            val name = binding.editTextName.text.toString()
            val email = binding.editTextEmail.text.toString()
            val password = binding.editTextPassword.text.toString()

            if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                // Register the new user
                userRepository.registerUser(name, email, password)

                // Show success message
                Toast.makeText(requireContext(), "tài khoản đã được thêm thành công ", Toast.LENGTH_SHORT).show()

                // Navigate back to AdminUserFragment
                findNavController().navigate(R.id.action_adduser_to_admin)
            } else {
                // Show error message if fields are empty
                Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
