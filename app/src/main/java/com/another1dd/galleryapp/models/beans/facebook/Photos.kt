package com.another1dd.galleryapp.models.beans.facebook

import com.google.gson.annotations.SerializedName


data class Photos(
        @SerializedName("data")
        var data: List<Datum>? = null,
        @SerializedName("paging")
        var paging: Paging? = null
)
