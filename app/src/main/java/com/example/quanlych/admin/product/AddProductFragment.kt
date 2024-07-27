package com.example.quanlych.admin.product

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import com.example.quanlych.R
import com.example.quanlych.data.DatabaseHelper
import com.example.quanlych.model.Category
import com.example.quanlych.model.Product

class AddProductFragment : Fragment() {

    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var editTextImageUrl: EditText
    private lateinit var spinnerProductCategory: Spinner
    private lateinit var categoryMap: Map<String, Int>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_product, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        databaseHelper = DatabaseHelper(requireContext())

        val editTextProductName: EditText = view.findViewById(R.id.editTextProductName)
        val editTextProductDescription: EditText = view.findViewById(R.id.editTextProductDescription)
        val editTextProductPrice: EditText = view.findViewById(R.id.editTextProductPrice)
        editTextImageUrl = view.findViewById(R.id.editTextImageUrl)
        spinnerProductCategory = view.findViewById(R.id.spinnerProductCategory)

        try {
            val categories = databaseHelper.getAllProductCategories()
            if (categories.isEmpty()) {
                Toast.makeText(requireContext(), "No categories found", Toast.LENGTH_SHORT).show()
                Log.d("AddProductFragment", "No categories found")
            } else {
                Log.d("AddProductFragment", "Categories found: ${categories.map { it.TenLoaiSanPham }}")
            }

            val categoryNames = categories.map { it.TenLoaiSanPham }
            categoryMap = categories.associateBy({ it.TenLoaiSanPham }, { it.MaLoaiSanPham })

            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categoryNames)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerProductCategory.adapter = adapter
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Error loading categories.", Toast.LENGTH_SHORT).show()
        }

        val buttonSaveProduct: Button = view.findViewById(R.id.buttonSaveProduct)

        buttonSaveProduct.setOnClickListener {
            val name = editTextProductName.text.toString()
            val description = editTextProductDescription.text.toString()
            val price = editTextProductPrice.text.toString().toDoubleOrNull() ?: 0.0
            val imageUrl = editTextImageUrl.text.toString()
            val selectedCategoryName = spinnerProductCategory.selectedItem.toString()
            val selectedCategoryId = categoryMap[selectedCategoryName] ?: 0

            if (name.isNotBlank() && description.isNotBlank() && imageUrl.isNotBlank()) {
                val product = Product(
                    id = 0,
                    name = name,
                    description = description,
                    imageResource = imageUrl,
                    price = price,
                    quantity = 1,
                    categoryId = selectedCategoryId
                )
                databaseHelper.addProduct(product)

                // Notify AdminProductFragment of the new product
                setFragmentResult("requestKey", bundleOf("newProductAdded" to true))
                findNavController().popBackStack()
            } else {
                Toast.makeText(requireContext(), "Please fill all fields.", Toast.LENGTH_SHORT).show()
            }
        }

    }
}
