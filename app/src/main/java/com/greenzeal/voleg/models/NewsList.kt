package com.greenzeal.voleg.models

import com.google.gson.annotations.SerializedName

data class NewsList (@SerializedName("pagination") var pagination: Pagination?,
                     @SerializedName("news") var news: ArrayList<News>?)