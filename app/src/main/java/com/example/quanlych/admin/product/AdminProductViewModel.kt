package com.example.quanlych.admin.product




import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AdminProductViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "SẢN PHẨM"
    }
    val text: LiveData<String> = _text
}