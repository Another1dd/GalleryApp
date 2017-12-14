package com.another1dd.galleryapp.extensions

import android.content.Context
import android.support.annotation.AnimRes
import android.view.animation.Animation
import android.view.animation.AnimationUtils


fun Context.loadAnimation(@AnimRes id: Int) = AnimationUtils.loadAnimation(applicationContext, id)!!

inline fun Animation.animListener(init: AnimListener.() -> Unit) = setAnimationListener(AnimListener().apply(init))

class AnimListener : Animation.AnimationListener {
    private var onAnimationRepeat: ((Animation?) -> Unit)? = null
    private var onAnimationEnd: ((Animation?) -> Unit)? = null
    private var onAnimationStart: ((Animation?) -> Unit)? = null

    override fun onAnimationRepeat(animation: Animation?) {
        onAnimationRepeat?.invoke(animation)
    }

    override fun onAnimationEnd(animation: Animation?) {
        onAnimationEnd?.invoke(animation)
    }

    override fun onAnimationStart(animation: Animation?) {
        onAnimationStart?.invoke(animation)
    }

    fun onAnimationRepeat(listener: (Animation?) -> Unit) {
        onAnimationRepeat = listener
    }

    fun onAnimationEnd(listener: (Animation?) -> Unit) {
        onAnimationEnd = listener
    }

    fun onAnimationStart(listener: (Animation?) -> Unit) {
        onAnimationStart = listener
    }
}