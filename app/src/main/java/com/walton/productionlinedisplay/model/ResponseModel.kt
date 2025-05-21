package com.walton.productionlinedisplay.model

data class ResponseModel(
    val barcode: List<String>,
    val not_received: Int,
    val pass: Int,
    val status: String
)