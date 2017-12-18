package com.another1dd.galleryapp.models.beans.facebook

import com.google.gson.annotations.SerializedName


data class Cursors(
        @SerializedName("before")
        var before: String? = null,
        @SerializedName("after")
        var after: String? = null
)
