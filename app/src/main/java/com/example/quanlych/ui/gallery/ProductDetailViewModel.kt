package com.example.quanlych.ui.gallery

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ProductDetailViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Trang chi tiet san phamr ne"
    }
    val text: LiveData<String> = _text
}