package com.greenzeal.voleg.models

import com.google.gson.annotations.SerializedName
import com.greenzeal.voleg.util.Auth
import java.util.*

data class Adv(
    @SerializedName("id") val id: Int?,
    @SerializedName("post_date") var postDate: String?,
    @SerializedName("adv_uuid") var advUuid: String?,
    @SerializedName("user_uuid") var userUuid: String?,
    @SerializedName("expire_date") var expireDate: String?,
    @SerializedName("adv_type") var advType: String?,
    @SerializedName("adv_text") var advText: String?,
    @SerializedName("image_url") val imageUrl: String?,
    @SerializedName("background_color") var backgroundColor: String?,
    @SerializedName("background_image_url") val backgroundImageUrl: String?,
    @SerializedName("adv_data") var advData: Any?,
    @SerializedName("approved") var approved: Int?,
    @SerializedName("post_date_timestamp") val postDateTimestamp: Int?,
    @SerializedName("expire_date_timestamp") val expireDateTimestamp: Int?
){
    constructor(): this(null, null, UUID.randomUUID().toString(), Auth.userId(), null, "text", "", null, "", null, "", null,null,null)
}



