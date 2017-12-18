package com.another1dd.galleryapp.models.beans.facebook

import com.google.gson.annotations.SerializedName


data class FacebookData(
        @SerializedName("data")
        var data: List<Data>? = null,
        @SerializedName("paging")
        var paging: Paging? = null
)