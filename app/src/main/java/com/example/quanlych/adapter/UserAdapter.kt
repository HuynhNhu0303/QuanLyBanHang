package com.example.quanlych.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.quanlych.R
import com.example.quanlych.data.User
import com.example.quanlych.databinding.AdminItemUserBinding


class UserAdapter(private var userList: List<User>) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    inner class UserViewHolder(private val binding: AdminItemUserBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User) {
            binding.username.text = user.name
            binding.email.text = user.email
            // Set default user image
            binding.UserImage.setImageResource(R.drawable.people)
            // Set details image
//            binding.imgdetails.setImageResource(R.drawable.details)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = AdminItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(userList[position])
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    fun updateData(newList: List<User>) {
        userList = newList
        notifyDataSetChanged()
    }
}