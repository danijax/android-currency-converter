package com.danijax.paypayxchange.api

import com.danijax.paypayxchange.model.ErrorResponse

sealed interface Result<out R> {
    data class Success<out T>(val data: T) : Result<T>

    data class Error(
        val errorResponse: ErrorResponse
    ) : Result<Nothing>

}