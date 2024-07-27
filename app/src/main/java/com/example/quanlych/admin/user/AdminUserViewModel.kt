package com.example.quanlych.admin.user



import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AdminUserViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "TÀI KHOẢN"
    }
    val text: LiveData<String> = _text
}