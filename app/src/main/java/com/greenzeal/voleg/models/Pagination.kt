package com.greenzeal.voleg.models

import com.google.gson.annotations.SerializedName

data class Pagination(
    @SerializedName("offset") val offset: Int?,
    @SerializedName("posts_per_page") val postsPerPage: Int?,
    @SerializedName("pageno") val pageno: Int?,
    @SerializedName("total_pages") val totalPages: Int?
)