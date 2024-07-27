package com.example.quanlych.data

import android.content.Context
import com.example.quanlych.model.Order

class OrderRepository(private val context: Context) {
    private val dbHelper = DatabaseHelper(context)

    fun getAllOrders(): List<Order> {
        return dbHelper.getAllOrders()
    }
    fun searchOrders(query: String): List<Order> {
        return dbHelper.searchOrders(query)  // Ensure this returns a List<Order>
    }
    fun getOrdersByDay(date: String): List<Order> {
        return dbHelper.getOrdersByDay(date)
    }

    fun getOrdersByWeek(week: Int, year: Int): List<Order> {
        return dbHelper.getOrdersByWeek(week, year)
    }

    fun getOrdersByMonth(month: Int, year: Int): List<Order> {
        return dbHelper.getOrdersByMonth(month, year)
    }

    fun getOrdersByYear(year: Int): List<Order> {
        return dbHelper.getOrdersByYear(year)
    }
}
