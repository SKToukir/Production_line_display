package com.walton.productionlinedisplay.api

import com.walton.productionlinedisplay.model.FinalQcApproval
import com.walton.productionlinedisplay.model.ResponseModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.QueryMap

interface ApiResponse {

    @GET("/api/tv/live_display/pass_but_not_received_barcode_tv.php")
    suspend fun getResponse(@QueryMap params: HashMap<String, String>): Response<ResponseModel>
}