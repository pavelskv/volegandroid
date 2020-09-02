package com.greenzeal.voleg.models

import com.google.gson.annotations.SerializedName

data class AdvData(
    @SerializedName("phone_numbers") val phoneNumbers: List<PhoneNumber>?,
    @SerializedName("accept_parcels") var acceptParcels: Int?
)