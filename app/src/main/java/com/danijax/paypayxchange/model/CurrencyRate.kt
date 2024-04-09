package com.danijax.paypayxchange.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CurrencyRate(val name: String, val rate: Float) : Parcelable