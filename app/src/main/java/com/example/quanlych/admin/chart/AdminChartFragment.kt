package com.example.quanlych.admin.chart

import AdminChartViewModel
import MonthPickerDialog
import YearPickerDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.DatePicker
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.quanlych.databinding.FragmentAdminChartBinding
import java.util.*

class AdminChartFragment : Fragment() {

    private var _binding: FragmentAdminChartBinding? = null
    private val binding get() = _binding!!
    private lateinit var adminChartViewModel: AdminChartViewModel

    private var currentSelectedDateType = -1 // Keep track of the current date type

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        adminChartViewModel = ViewModelProvider(this).get(AdminChartViewModel::class.java)
        _binding = FragmentAdminChartBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Observe LiveData to update UI
        adminChartViewModel.totalProductsCount.observe(viewLifecycleOwner) { count ->
            binding.sumproduct.text = count.toString()
        }

        adminChartViewModel.totalRevenue.observe(viewLifecycleOwner) { revenue ->
            binding.tongtien.text = "${revenue} VND"
        }


        adminChartViewModel.totalAccountCount.observe(viewLifecycleOwner) { count ->
            binding.sumuser.text = count.toString()
        }

        // Setup Spinner and Date Pickers
        setupSpinner()

        return root
    }

    private fun setupSpinner() {
        val spinner: Spinner = binding.spinnerDateType
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position != currentSelectedDateType) {
                    currentSelectedDateType = position
                    when (position) {
                        0 -> showDatePicker() // Date
                        1 -> showMonthPicker() // Month
                        2 -> showYearPicker() // Year
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Handle case where no item is selected
            }
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
            val selectedDate = "${selectedDay.toString().padStart(2, '0')}/${(selectedMonth + 1).toString().padStart(2, '0')}/$selectedYear"
            binding.textSelectedDate.text = selectedDate
            adminChartViewModel.loadProductsCount(selectedDate)
            adminChartViewModel.loadTotalRevenue(selectedDate)
            adminChartViewModel.loadAccountCount(selectedDate)
        }, year, month, day).show()
    }

    private fun showMonthPicker() {
        val dialog = MonthPickerDialog()
        dialog.setOnDateSetListener { selectedMonth ->
            val year = Calendar.getInstance().get(Calendar.YEAR)
            val formattedMonth = selectedMonth.toString().padStart(2, '0')
            val selectedDate = "$formattedMonth/$year"
            binding.textSelectedDate.text = selectedDate
            adminChartViewModel.loadProductsCount(selectedDate)
            adminChartViewModel.loadTotalRevenue(selectedDate)
            adminChartViewModel.loadAccountCount(selectedDate)
        }
        dialog.show(parentFragmentManager, "MonthPickerDialog")
    }

    private fun showYearPicker() {
        val dialog = YearPickerDialog()
        dialog.setOnDateSetListener { selectedYear ->
            binding.textSelectedDate.text = selectedYear.toString()
            adminChartViewModel.loadProductsCount(selectedYear.toString())
            adminChartViewModel.loadTotalRevenue(selectedYear.toString())
            adminChartViewModel.loadAccountCount(selectedYear.toString())
        }
        dialog.show(parentFragmentManager, "YearPickerDialog")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun updateSelectedDate(date: String) {
        binding.textSelectedDate.text = date
        adminChartViewModel.loadProductsCount(date)
        adminChartViewModel.loadTotalRevenue(date)
        adminChartViewModel.loadAccountCount(date)
    }
}