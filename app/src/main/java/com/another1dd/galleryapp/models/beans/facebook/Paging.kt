package com.another1dd.galleryapp.models.beans.facebook

import com.google.gson.annotations.SerializedName


data class Paging(
        @SerializedName("cursors")
        var cursors: Cursors? = null
)
