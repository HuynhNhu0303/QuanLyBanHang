
package com.example.quanlych.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quanlych.databinding.ItemOrderBinding
import com.example.quanlych.model.Order
import java.text.NumberFormat

class OrderAdapter(private var orders: List<Order>) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    inner class OrderViewHolder(private val binding: ItemOrderBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(order: Order) {
            binding.txtOrderId.text = order.id.toString()
            binding.txtOrderDate.text = order.date
            binding.txtOrderTotal.text = NumberFormat.getInstance().format(order.total) + " đ"
            binding.txtUserId.text = order.userId.toString() // Hiển thị userId
            // Display phone and address
            binding.txtOrderPhone.text = "Số điện thoại : ${order.phone}"
            binding.txtOrderAddress.text = "Địa chỉ: ${order.address}"

            // Set up the RecyclerView for order details
            binding.recyclerViewOrderDetails.layoutManager = LinearLayoutManager(binding.root.context)
            binding.recyclerViewOrderDetails.adapter = OrderDetailAdapter(order.orderDetails)
        }
    }

    fun updateOrders(newOrders: List<Order>) {
        orders = newOrders
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = ItemOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(orders[position])
    }

    override fun getItemCount(): Int = orders.size
}