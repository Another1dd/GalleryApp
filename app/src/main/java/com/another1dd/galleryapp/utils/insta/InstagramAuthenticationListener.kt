package com.another1dd.galleryapp.utils.insta


interface InstagramAuthenticationListener {
    fun onInstagramCodeReceived(accessToken: String?)
}
