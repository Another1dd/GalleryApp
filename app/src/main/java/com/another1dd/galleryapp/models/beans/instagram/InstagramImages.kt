package com.another1dd.galleryapp.models.beans.instagram

import com.google.gson.annotations.SerializedName

data class InstagramImages(
        @SerializedName("standard_resolution")
        val standardResolution: InstagramStandardResolution? = null
)
