package com.example.quanlych.ui.gallery

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.quanlych.R
import com.example.quanlych.data.DatabaseHelper
import com.example.quanlych.databinding.FragmentGalleryBinding
import com.example.quanlych.model.Category
import com.example.quanlych.model.Product
import com.example.quanlych.ui.home.ProductAdapter

class GalleryFragment : Fragment() {

    private var _binding: FragmentGalleryBinding? = null
    private val binding get() = _binding!!
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var productAdapter: ProductAdapter
    private lateinit var categories: List<Category> // Define Category class according to your database schema

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        val root: View = binding.root

        databaseHelper = DatabaseHelper(requireContext())

        // Fetch categories
        categories = databaseHelper.getAllCategories()
        val categoryNames = categories.map { it.TenLoaiSanPham }

        // Set up Spinner
        val spinnerFilter = binding.spinnerFilter
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categoryNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerFilter.adapter = adapter

        // Handle Spinner item selection
        spinnerFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedCategory = categories[position]
                filterProductsByCategory(selectedCategory.MaLoaiSanPham)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Handle case when nothing is selected
            }
        }

        // Set up RecyclerView
        val recyclerView = binding.recycleview
        recyclerView.layoutManager = GridLayoutManager(context, 2) // 2 columns
        productAdapter = ProductAdapter(emptyList(), object : ProductAdapter.OnItemClickListener {
            override fun onItemClick(product: Product) {
                val bundle = Bundle().apply {
                    putParcelable("product", product)
                }
                findNavController().navigate(R.id.action_gallery_to_productdetails, bundle)
            }
        })
        recyclerView.adapter = productAdapter

        // Set up EditText for real-time search
        val editTextSearch = binding.editText
        editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val query = s.toString()
                filterProducts(query)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // Initialize with all products
        filterProducts("") // Load all products initially

        return root
    }

    private fun filterProductsByCategory(categoryId: Int) {
        val filteredProducts = databaseHelper.getProductsByCategory(categoryId)
        productAdapter.updateProducts(filteredProducts)
    }

    private fun filterProducts(query: String) {
        val filteredProducts = databaseHelper.searchProducts(query)
        productAdapter.updateProducts(filteredProducts)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}