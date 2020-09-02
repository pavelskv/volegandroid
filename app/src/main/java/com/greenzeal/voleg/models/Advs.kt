package com.greenzeal.voleg.models

import com.google.gson.annotations.SerializedName

data class Advs(
    @SerializedName("pagination") var pagination: Pagination?,
    @SerializedName("advs") var advs: ArrayList<Adv>?
)