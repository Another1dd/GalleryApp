package com.another1dd.galleryapp.models.beans.instagram

import com.google.gson.annotations.SerializedName


data class InstagramUser(
        @SerializedName("profile_picture")
        val profilePicture: String? = null,
        @SerializedName("full_name")
        val fullName: String? = null
)