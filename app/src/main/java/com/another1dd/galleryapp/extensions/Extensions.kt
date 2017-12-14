package com.another1dd.galleryapp.extensions

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup


//Fragment inflate extension
fun ViewGroup.inflate(layoutId: Int, attachToRoot: Boolean = false): View =
        LayoutInflater.from(context).inflate(layoutId, this, attachToRoot)
