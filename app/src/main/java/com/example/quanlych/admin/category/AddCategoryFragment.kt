package com.example.quanlych.admin.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.example.quanlych.R
import com.example.quanlych.data.DatabaseHelper
import androidx.navigation.fragment.findNavController
class AddCategoryFragment : Fragment() {

    private lateinit var editTextCategoryName: EditText
    private lateinit var buttonSaveCategory: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_category, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        editTextCategoryName = view.findViewById(R.id.editTextCategoryName)

        buttonSaveCategory.setOnClickListener {
            saveCategory()
        }
    }

    private fun saveCategory() {
        val categoryName = editTextCategoryName.text.toString()

        if (categoryName.isNotBlank()) {
            val databaseHelper = DatabaseHelper(requireContext())
            databaseHelper.addCategory(categoryName)

            // Navigate back to the AdminCategoryFragment
            findNavController().popBackStack()
        } else {
            // Show error message if needed
        }
    }
}