package com.example.quanlych.ui.category

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.quanlych.R
import com.example.quanlych.model.Category

class CategoryAdapter(
    private var categories: List<Category>,
    private val onEditClick: (Category) -> Unit,
    private val onDeleteClick: (Category) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_category, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val category = categories[position]
        holder.categoryName.text = category.TenLoaiSanPham
        holder.editButton.setOnClickListener {
            onEditClick(category)
        }
        holder.deleteButton.setOnClickListener {
            onDeleteClick(category)
        }
    }

    override fun getItemCount(): Int = categories.size

    fun updateCategories(newCategories: List<Category>) {
        categories = newCategories
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val categoryName: TextView = itemView.findViewById(R.id.category_name)
        val editButton: ImageButton = itemView.findViewById(R.id.edit_button)
        val deleteButton: ImageButton = itemView.findViewById(R.id.delete_button)
    }
}
