package com.walton.productionlinedisplay.api

import com.walton.productionlinedisplay.model.FinalQcApproval
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.QueryMap

interface ApiResponseFinalQc {
    @GET("/api.php")
    suspend fun getFinalQcApproval(@QueryMap params: HashMap<String, String>): Response<FinalQcApproval>
}