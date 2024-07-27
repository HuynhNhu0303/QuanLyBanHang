package com.example.quanlych.admin.product

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.quanlych.R
import com.example.quanlych.data.DatabaseHelper
import com.example.quanlych.databinding.FragmentAdminProductBinding
import com.example.quanlych.model.Product

class AdminProductFragment : Fragment(), ProductAdapter.OnItemClickListener {

    private var _binding: FragmentAdminProductBinding? = null
    private val binding get() = _binding!!

    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var productAdapter: ProductAdapter
    private var productList: List<Product> = listOf()
    private var filteredList: List<Product> = listOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        databaseHelper = DatabaseHelper(requireContext())

        // Add test product for testing purposes
//        databaseHelper.addTestProduct()

        loadProducts()

        binding.floatingActionButton.setOnClickListener {
            findNavController().navigate(R.id.action_admin_to_addproduct)
        }

        binding.searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                filterProducts(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterProducts(newText)
                return true
            }
        })

        // Register listener for fragment result
        setFragmentResultListener("requestKey") { _, bundle ->
            val newProductAdded = bundle.getBoolean("newProductAdded", false)
            if (newProductAdded) {
                loadProducts() // Reload the product list after adding a new product
            }
        }
    }

    private fun loadProducts() {
        try {
            productList = databaseHelper.getAllProducts()
            filteredList = productList // Initialize filtered list
            updateRecyclerView()

        } catch (e: Exception) {
            Log.e("AdminProductFragment", "Error loading products", e)
        }
    }


    private fun filterProducts(query: String?) {
        val filtered = if (query.isNullOrEmpty()) {
            productList
        } else {
            productList.filter { product ->
                product.name.contains(query, ignoreCase = true) ||
                        product.description.contains(query, ignoreCase = true)
            }
        }
        filteredList = filtered
        updateRecyclerView()
    }

    private fun updateRecyclerView() {
        productAdapter = ProductAdapter(filteredList, this)
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = productAdapter
    }

    private fun showDeleteConfirmationDialog(product: Product) {
        AlertDialog.Builder(requireContext())
            .setTitle("Xóa sản phẩm")
            .setMessage("Bạn có chắc chắn muốn xóa sản phẩm này không?")
            .setPositiveButton("Xóa") { _, _ ->
                deleteProduct(product)
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    private fun deleteProduct(product: Product) {
        try {
            databaseHelper.deleteProduct(product.id.toInt())
            loadProducts() // Reload the product list after deletion
        } catch (e: Exception) {
            Log.e("AdminProductFragment", "Error deleting product", e)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onEditClick(product: Product) {
        val bundle = Bundle().apply {
            putInt("productId", product.id.toInt())
        }
        findNavController().navigate(R.id.action_admin_to_editproduct, bundle)
    }

    override fun onDeleteClick(product: Product) {
        showDeleteConfirmationDialog(product)
    }
}
