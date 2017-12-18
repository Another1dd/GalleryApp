package com.another1dd.galleryapp.models.beans.facebook

import com.google.gson.annotations.SerializedName


data class Image(
        @SerializedName("source")
        var source: String? = null
)