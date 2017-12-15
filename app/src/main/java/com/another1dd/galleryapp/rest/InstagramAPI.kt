package com.another1dd.galleryapp.rest

import com.another1dd.galleryapp.models.beans.instagram.InstagramResponse
import retrofit2.Call


interface InstagramAPI {
    fun getPhotos(accessToken: String): Call<InstagramResponse>
}