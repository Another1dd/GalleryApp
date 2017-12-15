package com.another1dd.galleryapp.models.beans.instagram

import com.google.gson.annotations.SerializedName

data class InstagramData(
        @SerializedName("images")
        val images: InstagramImages? = null,
        @SerializedName("user")
        val user: InstagramUser? = null
)
