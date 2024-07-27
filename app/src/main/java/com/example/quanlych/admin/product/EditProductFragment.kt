package com.example.quanlych.admin.product

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.quanlych.R
import com.example.quanlych.data.DatabaseHelper
import com.example.quanlych.databinding.FragmentEditProductBinding
import com.example.quanlych.model.Category
import com.example.quanlych.model.Product

class EditProductFragment : Fragment() {

    private var _binding: FragmentEditProductBinding? = null
    private val binding get() = _binding!!
    private lateinit var databaseHelper: DatabaseHelper
    private var productId: Int = -1
    private var categories: List<Category> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        databaseHelper = DatabaseHelper(requireContext())

        // Get product ID from arguments
        arguments?.let {
            productId = it.getInt("productId", -1)
            if (productId != -1) {
                // Load categories first
                categories = databaseHelper.getAllProductCategories()
                setupCategorySpinner()

                // Then load product details
                loadProductDetails(productId)
            } else {
                showError("Invalid Product ID")
            }
        }

        binding.saveButton.setOnClickListener {
            saveProduct()
        }
    }

    private fun setupCategorySpinner() {
        val categoryNames = categories.map { it.TenLoaiSanPham }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categoryNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCategory.adapter = adapter
    }

    private fun loadProductDetails(productId: Int) {
        val product = databaseHelper.getProductById(productId)
        product?.let {
            binding.editProductName.setText(it.name)
            binding.editProductDescription.setText(it.description)
            binding.editProductPrice.setText(it.price.toString())
            binding.editProductQuantity.setText(it.quantity.toString())
            binding.editImageUrl.setText(it.imageResource)

            // Set selected category
            val categoryIndex = categories.indexOfFirst { it.MaLoaiSanPham == product.categoryId }
            if (categoryIndex != -1) {
                binding.spinnerCategory.setSelection(categoryIndex)
            }
        } ?: showError("Product not found")
    }

    private fun saveProduct() {
        val name = binding.editProductName.text.toString()
        val description = binding.editProductDescription.text.toString()
        val price = binding.editProductPrice.text.toString().toDoubleOrNull()
        val quantity = binding.editProductQuantity.text.toString().toIntOrNull()
        val imageUrl = binding.editImageUrl.text.toString()
        val selectedCategoryPosition = binding.spinnerCategory.selectedItemPosition

        if (name.isBlank() || description.isBlank() || imageUrl.isBlank()) {
            showError("Please fill in all fields.")
            return
        }

        if (price == null || quantity == null) {
            showError("Invalid input. Please check the fields.")
            return
        }

        if (selectedCategoryPosition == -1) {
            showError("Please select a category.")
            return
        }

        val selectedCategoryId = categories[selectedCategoryPosition].MaLoaiSanPham

        val product = Product(
            id = productId,
            name = name,
            description = description,
            imageResource = imageUrl,
            price = price,
            quantity = quantity,
            categoryId = selectedCategoryId
        )

        databaseHelper.updateProduct(product)
        findNavController().popBackStack()
    }

    private fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}