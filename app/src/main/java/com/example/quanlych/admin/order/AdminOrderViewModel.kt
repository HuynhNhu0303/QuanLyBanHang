package com.example.quanlych.admin.order

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.quanlych.data.OrderRepository
import com.example.quanlych.model.Order

class AdminOrderViewModel(private val repository: OrderRepository) : ViewModel() {

    private val _orders = MutableLiveData<List<Order>>()
    val orders: LiveData<List<Order>> = _orders

    init {
        _orders.value = repository.getAllOrders()
    }

    fun getAllOrders(): List<Order> {
        return repository.getAllOrders()
    }
    fun searchOrders(query: String) {
        _orders.value = repository.searchOrders(query)
    }
    fun getOrdersByDay(date: String): List<Order> {
        return repository.getOrdersByDay(date)
    }

    fun getOrdersByWeek(week: Int, year: Int): List<Order> {
        return repository.getOrdersByWeek(week, year)
    }

    fun getOrdersByMonth(month: Int, year: Int): List<Order> {
        return repository.getOrdersByMonth(month, year)
    }

    fun getOrdersByYear(year: Int): List<Order> {
        return repository.getOrdersByYear(year)
    }
}
