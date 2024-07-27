package com.example.quanlych.admin.order

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.quanlych.adapter.OrderAdapter
import com.example.quanlych.databinding.FragementAdminOrderBinding
import com.example.quanlych.data.OrderRepository
import com.example.quanlych.model.Order
import java.text.SimpleDateFormat
import java.util.*

class AdminOrderFragment : Fragment() {

    private var _binding: FragementAdminOrderBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: AdminOrderViewModel
    private lateinit var viewModelFactory: AdminOrderViewModelFactory

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragementAdminOrderBinding.inflate(inflater, container, false)
        val repository = OrderRepository(requireContext())
        viewModelFactory = AdminOrderViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(AdminOrderViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup Spinner
        val filterOptions = arrayOf("Tất cả", "Ngày", "Tuần", "Tháng", "Năm")
        val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, filterOptions)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerFilter.adapter = spinnerAdapter

        binding.spinnerFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                filterOrders(filterOptions[position])
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Handle case when nothing is selected
            }
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        viewModel.orders.observe(viewLifecycleOwner) { orders ->
            binding.recyclerView.adapter = OrderAdapter(orders)
        }

        // Handle search input
        binding.editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // No-op
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                s?.let {
                    viewModel.searchOrders(it.toString())
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // No-op
            }
        })
    }

    private fun filterOrders(filter: String) {
        val filteredOrders: List<Order> = when (filter) {
            "Ngày" -> viewModel.getOrdersByDay(getCurrentDate())
            "Tuần" -> viewModel.getOrdersByWeek(getCurrentWeek(), getCurrentYear())
            "Tháng" -> viewModel.getOrdersByMonth(getCurrentMonth(), getCurrentYear())
            "Năm" -> viewModel.getOrdersByYear(getCurrentYear())
            else -> viewModel.getAllOrders()
        }
        Log.d("AdminOrderFragment", "Filtered Orders: $filteredOrders")
        (binding.recyclerView.adapter as? OrderAdapter)?.updateOrders(filteredOrders)
    }

    private fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    private fun getCurrentWeek(): Int {
        val calendar = Calendar.getInstance()
        return calendar.get(Calendar.WEEK_OF_YEAR)
    }

    private fun getCurrentMonth(): Int {
        val calendar = Calendar.getInstance()
        return calendar.get(Calendar.MONTH) + 1
    }

    private fun getCurrentYear(): Int {
        val calendar = Calendar.getInstance()
        return calendar.get(Calendar.YEAR)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
