package com.dazone.crewchatoff.activity.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dazone.crewchatoff.dto.ErrorDto

open class BaseViewModel : ViewModel() {
    val eventLoading = MutableLiveData<Boolean>()
    val eventError = MutableLiveData<ErrorDto>()

    init {
        showLoading(false)
    }

    fun showLoading(value: Boolean) {
        eventLoading.postValue(value)
    }

    fun showError(error: ErrorDto) {
        eventError.postValue(error)
    }
}