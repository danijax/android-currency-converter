package com.danijax.paypayxchange.api

import com.danijax.paypayxchange.model.ErrorResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.serialization.json.Json
import retrofit2.Call


fun <T> apiRequestFlow(call: suspend () -> Call<T>) = channelFlow<Result<T>> {
    val response = call().execute()

    if (response.isSuccessful) {
        response.body()?.let { data ->
            send(Result.Success(data))
        }
    } else {
        when (response.code()) {
            500, 501, 502, 503, 504, 509, 511 -> {
                send(
                    Result.Error(
                        ErrorResponse(
                            error = true,
                            status = response.code(),
                            message = "A Server Error has occurred",
                            description = "An error has occurred from the server please try again"
                        )
                    )
                )
            }

            else -> {
                response.errorBody()?.let { error ->
                    val er = error.string()

                    val parsedErrors = Json.decodeFromString<ErrorResponse>(er)

                    send(Result.Error( errorResponse = parsedErrors,) )


                }
            }
        }
    }

}.flowOn(Dispatchers.IO)