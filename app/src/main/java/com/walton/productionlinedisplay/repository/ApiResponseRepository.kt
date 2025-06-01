package com.walton.productionlinedisplay.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.walton.productionlinedisplay.api.ApiResponse
import com.walton.productionlinedisplay.api.ApiResponseFinalQc
import com.walton.productionlinedisplay.model.FinalQcApproval
import com.walton.productionlinedisplay.model.ResponseModel
import com.walton.productionlinedisplay.utils.Constant.TAG
import com.walton.productionlinedisplay.utils.NetworkResult
import org.json.JSONObject
import retrofit2.Response
import javax.inject.Inject

class ApiResponseRepository @Inject constructor(private val apiResponse: ApiResponse, private val apiResponseFinalQc: ApiResponseFinalQc) {

    private val _apiResponseLiveData = MutableLiveData<NetworkResult<ResponseModel>>()
    val apiResponseLiveData: LiveData<NetworkResult<ResponseModel>> get() = _apiResponseLiveData

    private val _finalQcResponse = MutableLiveData<NetworkResult<FinalQcApproval>>()
    val apiResponseFinalQcApproval: LiveData<NetworkResult<FinalQcApproval>> get() = _finalQcResponse

    suspend fun getResponse(params: HashMap<String, String>) {
        _apiResponseLiveData.postValue(NetworkResult.Loading())
        try {
            val response = apiResponse.getResponse(params)
            Log.d(TAG, "getResponse: $response")
            handleResponse(response)
        } catch (e: Exception) {
            e.printStackTrace()
            _apiResponseLiveData.postValue(NetworkResult.Error("Network error occurred: ${e.message}"))
            Log.d(TAG, "getResponse: " + e.localizedMessage)
        }
    }


    suspend fun getFinalQcApprovalResponse(params: HashMap<String, String>) {
        _finalQcResponse.postValue(NetworkResult.Loading())
        try {
            val response = apiResponseFinalQc.getFinalQcApproval(params)
            handleResponseFinalQc(response)
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d(TAG, "getFinalQcApprovalResponse: error ${e.message}")
            _finalQcResponse.postValue(NetworkResult.Error("Network error occurred: ${e.message}"))
        }
    }


    private fun handleResponseFinalQc(response: Response<FinalQcApproval>) {
        Log.d(TAG, "handleResponseFinalQc: ---- ${response.body()}")
        if (response.isSuccessful && response.body() != null) {
            val data = response.body()
            _finalQcResponse.postValue(NetworkResult.Success(data!!))
        } else if (response.errorBody() != null) {
            val errorObject = JSONObject(response.errorBody()!!.charStream().readText())
            _finalQcResponse.postValue(NetworkResult.Error(errorObject.getString("message")))
        } else {
            _finalQcResponse.postValue(NetworkResult.Error("Something went wrong"))
        }
    }


    private fun handleResponse(response: Response<ResponseModel>) {
        if (response.isSuccessful && response.body() != null) {
            val data = response.body()
            _apiResponseLiveData.postValue(NetworkResult.Success(data!!))
        } else if (response.errorBody() != null) {
            val errorObject = JSONObject(response.errorBody()!!.charStream().readText())
            _apiResponseLiveData.postValue(NetworkResult.Error(errorObject.getString("message")))
        } else {
            _apiResponseLiveData.postValue(NetworkResult.Error("Something went wrong"))
        }
    }
}