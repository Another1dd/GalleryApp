package com.another1dd.galleryapp.extensions

import android.view.View


fun View.gone() {
    this.visibility = View.GONE
}

fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.invisible() {
    this.visibility = View.INVISIBLE
}

fun View.isVisible(): Boolean = this.visibility == View.VISIBLE

fun View.isGone(): Boolean = this.visibility == View.GONE

fun setViewsGone(vararg views: View) {
    for (view in views) {
        view.gone()
    }
}

fun setViewsVisible(vararg views: View) {
    for (view in views) {
        view.visible()
    }
}

fun setViewsInvisible(vararg views: View) {
    for (view in views) {
        view.invisible()
    }
}
