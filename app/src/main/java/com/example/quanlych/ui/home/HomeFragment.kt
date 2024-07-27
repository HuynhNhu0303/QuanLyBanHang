package com.example.quanlych.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ViewFlipper
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.quanlych.R
import com.example.quanlych.data.DatabaseHelper
import com.example.quanlych.databinding.FragmentHomeBinding
import com.example.quanlych.model.Product

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewFlipper: ViewFlipper

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Accessing ViewFlipper
        viewFlipper = binding.viewFlipper
        viewFlipper.startFlipping()

        // Set up TextView from ViewModel
        homeViewModel.text.observe(viewLifecycleOwner) {
            binding.textHome.text = it
        }

        // Initialize DatabaseHelper
        val databaseHelper = DatabaseHelper(requireContext())

        // Fetch featured products sorted by quantity sold
        val featuredProducts = databaseHelper.getProductsByQuantitySold()
        binding.recycleviewFeatured.layoutManager = GridLayoutManager(context, 2) // 2 columns
        val featuredAdapter = ProductAdapter(featuredProducts, object : ProductAdapter.OnItemClickListener {
            override fun onItemClick(product: Product) {
                // Create a Bundle to pass product details
                val bundle = Bundle().apply {
                    putParcelable("product", product)
                }
                // Navigate to the product detail screen
                findNavController().navigate(R.id.action_home_to_productdetails, bundle)
            }
        })
        binding.recycleviewFeatured.adapter = featuredAdapter

        // Fetch all products
        val allProducts = databaseHelper.getAllProducts() // Add this method in your DatabaseHelper class
        binding.recycleviewAll.layoutManager = GridLayoutManager(context, 2) // 2 columns
        val allAdapter = ProductAdapter(allProducts, object : ProductAdapter.OnItemClickListener {
            override fun onItemClick(product: Product) {
                // Create a Bundle to pass product details
                val bundle = Bundle().apply {
                    putParcelable("product", product)
                }
                // Navigate to the product detail screen
                findNavController().navigate(R.id.action_home_to_productdetails, bundle)
            }
        })
        binding.recycleviewAll.adapter = allAdapter

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
