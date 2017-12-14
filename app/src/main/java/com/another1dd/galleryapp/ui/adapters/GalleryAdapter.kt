package com.another1dd.galleryapp.ui.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.another1dd.galleryapp.R
import com.another1dd.galleryapp.models.Image
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.gallery_item.view.*


class GalleryAdapter(private val context: Context,
                     private val images: ArrayList<Image>) : RecyclerView.Adapter<GalleryAdapter.ViewHolder>() {
    private val selectedImages = ArrayList(images.filter { it.isSelected })

    override fun getItemCount(): Int {
        return images.size
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        holder?.bindImage(images[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.gallery_item, parent, false)
        return ViewHolder(view, context, selectedImages, this)
    }


    inner class ViewHolder(view: View, private val context: Context,
                           private val selectedImages: ArrayList<Image>,
                           private val galleryAdapter: GalleryAdapter) : RecyclerView.ViewHolder(view) {
        fun bindImage(image: Image) {
            Glide.with(context).load(image.path).centerCrop().into(itemView.galleryItemIv)

            if (image.isSelected) {
                itemView.galleryItemSelectedBlock.visibility = View.VISIBLE
                itemView.galleryItemSelectedTv.text = (selectedImages.indexOf(image) + 1).toString()
            } else {
                itemView.galleryItemSelectedBlock.visibility = View.GONE
            }

            itemView.galleryItemIv.setOnClickListener {
                if (!image.isSelected) {
                    image.isSelected = true
                    selectedImages.add(image)

                    itemView.galleryItemSelectedTv.text = (selectedImages.indexOf(image) + 1).toString()
                    itemView.galleryItemSelectedBlock.visibility = View.VISIBLE
                } else {
                    image.isSelected = false
                    itemView.galleryItemSelectedBlock.visibility = View.GONE
                    val i = selectedImages.indexOf(image)
                    selectedImages.remove(image)

                    selectedImages.forEachIndexed { index, image ->
                        if (index >= i) {
                            galleryAdapter.notifyItemChanged(images.indexOf(image))
                        }
                    }
                }
            }
        }
    }
}
