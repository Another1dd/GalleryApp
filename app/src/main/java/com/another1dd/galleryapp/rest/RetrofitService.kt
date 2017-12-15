package com.another1dd.galleryapp.rest

import com.another1dd.galleryapp.models.beans.instagram.InstagramResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


interface RetrofitService {
    @GET("v1/users/self/media/recent")
    fun getTagPhotos(@Query("access_token") access_token: String): Call<InstagramResponse>
}

