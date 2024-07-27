package com.example.quanlych.ui.order

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.quanlych.R
import com.example.quanlych.databinding.FragmentOrderBinding
import com.example.quanlych.model.Product
import com.example.quanlych.data.DatabaseHelper
import com.example.quanlych.data.UserRepository
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class OrderFragment : Fragment() {

    private var _binding: FragmentOrderBinding? = null
    private val binding get() = _binding!!
    private lateinit var databaseHelper: DatabaseHelper
    private var selectedProducts: List<Product> = ArrayList()
    private var userEmail: String = ""
    private var totalPrice: Double = 0.0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrderBinding.inflate(inflater, container, false)
        val root: View = binding.root

        databaseHelper = DatabaseHelper(requireContext())

        // Retrieve arguments
        totalPrice = arguments?.getDouble("totalPrice") ?: 0.0
        selectedProducts = arguments?.getParcelableArrayList<Product>("selectedProducts") ?: ArrayList()
        userEmail = UserRepository.UserSession.email ?: ""
        Log.d("OrderFragment", "User Email: $userEmail")

        // Initialize the UI
        val formattedPrice = NumberFormat.getInstance(Locale.getDefault()).format(totalPrice)
        binding.txttongtien.text = "$formattedPrice đ"
        binding.edtdiachi.setText("")
        binding.edtphone.setText("")
        binding.txtemaildathang.text = userEmail

        // Handle order button click
        binding.btndathang.setOnClickListener {
            placeOrder()
        }

        return root
    }


    private fun placeOrder() {
        val phoneNumber = binding.edtphone.text.toString().trim()
        val address = binding.edtdiachi.text.toString().trim()

        // Validate input
        if (phoneNumber.isEmpty() || address.isEmpty()) {
            Toast.makeText(requireContext(), "Vui lòng nhập số điện thoại và địa chỉ", Toast.LENGTH_SHORT).show()
            return
        }

        // Insert order into database
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val userId = databaseHelper.getUserIdByEmail(userEmail)

        if (userId == -1) { // Check for invalid user ID
            Toast.makeText(requireContext(), "Người dùng không tồn tại", Toast.LENGTH_SHORT).show()
            return
        }

        // Insert order
        val orderId = databaseHelper.addOrder(userId, currentDate, totalPrice, address, phoneNumber, 1) // 1 is default payment method

        // Insert order details and update product quantities
        for (product in selectedProducts) {
            databaseHelper.addOrderDetail(orderId, product.id, product.quantity, product.price)
            databaseHelper.updateProductQuantity(product.id, product.quantity)
        }

        Toast.makeText(requireContext(), "Đặt hàng thành công", Toast.LENGTH_SHORT).show()

        // Navigate to SlideshowFragment
        val navController = findNavController()
        navController.navigate(R.id.action_order_to_slideshow)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
