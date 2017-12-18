package com.another1dd.galleryapp.models.beans.facebook

import com.google.gson.annotations.SerializedName


data class Datum(
        @SerializedName("images")
        var images: List<Image>? = null,
        @SerializedName("id")
        var id: String? = null
)