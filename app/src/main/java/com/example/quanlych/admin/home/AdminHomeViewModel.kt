package com.example.quanlych.admin.home


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AdminHomeViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Xin Chao"
    }
    val text: LiveData<String> = _text
}