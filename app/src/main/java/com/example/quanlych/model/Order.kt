package com.example.quanlych.model

data class Order(
    val id: Int,
    val userId: Int,
    val date: String,
    val total: Double,
    val status: Int,
    val address: String,
    val phone: String,
    val paymentMethod: Int,
    var orderDetails: List<OrderDetail> = mutableListOf() // Make this mutable to allow updates
)

data class OrderDetail(
    val productId: Int,
    val productName: String,
    val quantity: Int,
    val price: Double,
    val productImage: String
)



