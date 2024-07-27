package com.example.quanlych.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.quanlych.R
import com.example.quanlych.model.OrderDetail
import java.text.NumberFormat

class OrderDetailAdapter(private val orderDetails: List<OrderDetail>) : RecyclerView.Adapter<OrderDetailAdapter.OrderDetailViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderDetailViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_order_detail, parent, false)
        return OrderDetailViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderDetailViewHolder, position: Int) {
        val detail = orderDetails[position]
        holder.bind(detail)
    }

    override fun getItemCount(): Int = orderDetails.size

    class OrderDetailViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(detail: OrderDetail) {
            itemView.findViewById<TextView>(R.id.txtProductName).text = detail.productName
            itemView.findViewById<TextView>(R.id.txtProductQuantity).text = "Số lượng: ${detail.quantity}"
            itemView.findViewById<TextView>(R.id.txtProductPrice).text = "Giá: ${detail.price}"
            // Load image using a library like Glide or Picasso
            Glide.with(itemView.context).load(detail.productImage).into(itemView.findViewById(R.id.imgProduct))
        }
    }
}
