package com.example.quanlych.ui.Cart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CartViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Gio hang trong"
    }
    val text: LiveData<String> = _text
}