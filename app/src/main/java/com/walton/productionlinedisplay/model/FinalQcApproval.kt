package com.walton.productionlinedisplay.model

data class FinalQcApproval(
    val data: LineData,
    val msg: String,
    val status: Int,
    val success: Boolean,
    val total_count: Int
)