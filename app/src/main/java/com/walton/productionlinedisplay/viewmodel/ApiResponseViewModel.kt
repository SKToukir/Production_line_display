package com.walton.productionlinedisplay.viewmodel

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.walton.productionlinedisplay.model.FinalQcApproval
import com.walton.productionlinedisplay.model.ResponseModel
import com.walton.productionlinedisplay.repository.ApiResponseRepository
import com.walton.productionlinedisplay.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class ApiResponseViewModel @Inject constructor(private val apiResponseRepo: ApiResponseRepository) :
    ViewModel() {

    val apiResponseLiveData: LiveData<NetworkResult<ResponseModel>> get() = apiResponseRepo.apiResponseLiveData
    val apiResponseFinalQcApproval: LiveData<NetworkResult<FinalQcApproval>> get() = apiResponseRepo.apiResponseFinalQcApproval

    fun getResponse(params: HashMap<String, String>) {
        viewModelScope.launch {
            apiResponseRepo.getResponse(params)
        }
    }

    fun getFinalQcApproval(params: HashMap<String, String>) {
        viewModelScope.launch {
            apiResponseRepo.getFinalQcApprovalResponse(params)
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun getCurrentDate(isDisplay: Boolean): String {
        val formatter = SimpleDateFormat(
            if (isDisplay) {
                "dd-MM-yyyy"
            } else {
                "yyyy-MM-dd"
            }
        )
        val date = Date()
        return formatter.format(date)
    }
}