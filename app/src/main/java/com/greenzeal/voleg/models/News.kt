package com.greenzeal.voleg.models

import com.google.gson.annotations.SerializedName

data class News(
    @SerializedName("id") val id: Int?,
    @SerializedName("title") var title: String?,
    @SerializedName("content") var content: String?,
    @SerializedName("url") var url: String?,
    @SerializedName("image") var image: String?,
    @SerializedName("uid") var uid: String?,
    @SerializedName("date") var date: String?,
    @SerializedName("source") val source: String?,
    @SerializedName("post_date_timestamp") val postDateTimestamp: Int?
)