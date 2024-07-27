package com.example.quanlych.ui.slideshow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.quanlych.adapter.OrderAdapter
import com.example.quanlych.data.DatabaseHelper
import com.example.quanlych.data.UserRepository
import com.example.quanlych.databinding.FragmentSlideshowBinding
import com.example.quanlych.model.Order

class SlideshowFragment : Fragment() {

    private var _binding: FragmentSlideshowBinding? = null
    private val binding get() = _binding!!
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var orderAdapter: OrderAdapter
    private var orderList: List<Order> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSlideshowBinding.inflate(inflater, container, false)
        val root: View = binding.root

        databaseHelper = DatabaseHelper(requireContext())

        // Retrieve user email from UserRepository
        val userEmail = UserRepository.UserSession.email ?: ""
        val userId = databaseHelper.getUserIdByEmail(userEmail)

        // Fetch orders based on userId
        orderList = if (userId != -1) {
            databaseHelper.getOrdersByUserIdWithDetails(userId)
        } else {
            ArrayList()
        }

        // Setup RecyclerView
        orderAdapter = OrderAdapter(orderList)
        binding.recyclerViewOrders.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewOrders.adapter = orderAdapter

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
