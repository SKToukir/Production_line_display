package com.walton.productionlinedisplay.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.walton.productionlinedisplay.model.ResponseModel
import com.walton.productionlinedisplay.repository.ApiResponseRepository
import com.walton.productionlinedisplay.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ApiResponseViewModel @Inject constructor(private val apiResponseRepo: ApiResponseRepository): ViewModel() {

    val apiResponseLiveData: LiveData<NetworkResult<ResponseModel>> get() = apiResponseRepo.apiResponseLiveData

    fun getResponse(params: HashMap<String, String>){
        viewModelScope.launch{
            apiResponseRepo.getResponse(params)
        }
    }
}