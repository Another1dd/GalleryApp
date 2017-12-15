package com.another1dd.galleryapp.rest

import com.another1dd.galleryapp.models.constants.Instagram
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RestClient {
    val retrofitService: RetrofitService
        get() = Retrofit.Builder()
                .baseUrl(Instagram.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(RetrofitService::class.java)
}
