package com.another1dd.galleryapp.ui.adapters.adjustment

import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.view.View

class AdjustmentItemDecorator(private val spacing : Int) : RecyclerView.ItemDecoration(){
    override fun getItemOffsets(outRect: Rect, view: View?, parent: RecyclerView, state: RecyclerView.State?) {
        if (parent.getChildAdapterPosition(view) == 0) {
            outRect.left = spacing
        }

        outRect.right = spacing
        outRect.top = spacing
        outRect.bottom = spacing
    }
}
