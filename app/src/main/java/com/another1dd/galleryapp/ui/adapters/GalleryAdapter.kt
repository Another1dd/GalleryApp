package com.another1dd.galleryapp.ui.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.another1dd.galleryapp.R
import com.bumptech.glide.Glide
import com.another1dd.galleryapp.models.Image
import kotlinx.android.synthetic.main.gallery_item.view.*


class GalleryAdapter(private val context: Context,
                     private val images: ArrayList<Image>) : RecyclerView.Adapter<GalleryAdapter.ViewHolder>() {
    override fun getItemCount(): Int {
        return images.size
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        holder?.bindImage(images[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.gallery_item, parent, false)
        return ViewHolder(view, context)
    }


    class ViewHolder(view: View, private val context: Context) : RecyclerView.ViewHolder(view) {
        fun bindImage(image: Image) {
            Glide.with(context).load(image.path).centerCrop().into(itemView.galleryItemIv)
        }
    }
}
