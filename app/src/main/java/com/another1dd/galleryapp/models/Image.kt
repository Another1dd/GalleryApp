package com.another1dd.galleryapp.models

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@SuppressLint("ParcelCreator")
@Parcelize
data class Image(
        var id: Long = 0,
        var name: String,
        var path: String,
        var isSelected: Boolean = false
) : Parcelable
