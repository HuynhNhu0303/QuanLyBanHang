package com.example.quanlych.ui.slideshow

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SlideshowViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Bạn chưa có đơn hàng nào"
    }
    val text: LiveData<String> = _text
}