package com.example.quanlych.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RegisterViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Đăng ký"
    }
    val text: LiveData<String> = _text
}