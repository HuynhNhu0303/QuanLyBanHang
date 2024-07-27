package com.example.quanlych.utils

import com.example.quanlych.model.Product

object CartManager {
    val cartItems = mutableListOf<Product>()

    fun addProductToCart(product: Product) {
        val existingProduct = cartItems.find { it.id == product.id }
        if (existingProduct != null) {
            // If product exists, update its quantity
            existingProduct.quantity += product.quantity
        } else {
            // If product does not exist, add it to the cart
            cartItems.add(product)
        }
    }

    fun removeProductFromCart(product: Product) {
        cartItems.removeIf { it.id == product.id }
    }
}
