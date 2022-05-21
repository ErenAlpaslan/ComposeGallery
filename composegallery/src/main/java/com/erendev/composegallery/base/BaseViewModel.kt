package com.erendev.composegallery.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

internal open class BaseViewModel: ViewModel() {

    private val _showProgress: MutableLiveData<Boolean> = MutableLiveData(false)
    val showProgress: LiveData<Boolean>
        get() = _showProgress

    val errorMessage: MutableLiveData<String> = MutableLiveData()

    fun showProgress() = viewModelScope.launch{
        _showProgress.postValue(true)
    }

    fun hideProgress() = viewModelScope.launch {
        _showProgress.postValue(false)
    }


}