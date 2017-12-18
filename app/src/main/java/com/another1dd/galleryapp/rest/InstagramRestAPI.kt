package com.another1dd.galleryapp.rest

import com.another1dd.galleryapp.models.beans.instagram.InstagramResponse
import retrofit2.Call
import javax.inject.Inject

class InstagramRestAPI @Inject constructor(private val retrofitService: RetrofitService) : InstagramAPI {
    override fun getPhotos(accessToken: String, count: Int): Call<InstagramResponse> {
        return retrofitService.getTagPhotos(accessToken, count)
    }
}
