package com.another1dd.galleryapp.models.beans.instagram

import com.google.gson.annotations.SerializedName

data class InstagramResponse(
        @SerializedName("data")
        val data: ArrayList<InstagramData>?= null
)
