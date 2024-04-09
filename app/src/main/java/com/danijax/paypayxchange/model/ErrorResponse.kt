package com.danijax.paypayxchange.model

data class ErrorResponse(
    val description: String,
    val error: Boolean,
    val message: String,
    val status: Int
)