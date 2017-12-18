package com.another1dd.galleryapp.models.beans.facebook

import com.google.gson.annotations.SerializedName


data class Data(
        @SerializedName("photos")
        var photos: Photos? = null,
        @SerializedName("id")
        var id: String? = null
)
