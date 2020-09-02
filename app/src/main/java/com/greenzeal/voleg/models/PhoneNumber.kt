package com.greenzeal.voleg.models

import com.google.gson.annotations.SerializedName

data class PhoneNumber(
    @SerializedName("number") var number: String?,
    @SerializedName("call_providers") var callProviders: MutableList<String>?
)

