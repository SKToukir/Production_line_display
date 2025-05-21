package com.walton.productionlinedisplay.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.walton.productionlinedisplay.api.ApiResponse
import com.walton.productionlinedisplay.model.ResponseModel
import com.walton.productionlinedisplay.utils.Constant.TAG
import com.walton.productionlinedisplay.utils.NetworkResult
import org.json.JSONObject
import retrofit2.Response
import javax.inject.Inject

class ApiResponseRepository @Inject constructor(private val apiResponse: ApiResponse) {

    private val _apiResponseLiveData = MutableLiveData<NetworkResult<ResponseModel>>()
    val apiResponseLiveData: LiveData<NetworkResult<ResponseModel>> get() = _apiResponseLiveData

    suspend fun getResponse(params: HashMap<String, String>) {
        _apiResponseLiveData.postValue(NetworkResult.Loading())
        try {
            val response = apiResponse.getResponse(params)
            handleResponse(response)
        } catch (e: Exception) {
            e.printStackTrace()
            _apiResponseLiveData.postValue(NetworkResult.Error("Network error occurred: ${e.message}"))
            Log.d(TAG, "getResponse: "+e.localizedMessage)
        }
    }

    private fun handleResponse(response: Response<ResponseModel>) {
        if (response.isSuccessful && response.body() != null){
            val data = response.body()
            _apiResponseLiveData.postValue(NetworkResult.Success(data!!))
        }else if (response.errorBody() != null){
            val errorObject = JSONObject(response.errorBody()!!.charStream().readText())
            _apiResponseLiveData.postValue(NetworkResult.Error(errorObject.getString("message")))
        }else{
            _apiResponseLiveData.postValue(NetworkResult.Error("Something went wrong"))
        }
    }
}