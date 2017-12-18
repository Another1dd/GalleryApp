package com.another1dd.galleryapp.ui.adapters.order

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.another1dd.galleryapp.R
import com.another1dd.galleryapp.models.Image
import com.another1dd.galleryapp.utils.rx.RxArrayList
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.order_item.view.*

class OrderAdapter(private val context: Context,
                   private val selectedImages: RxArrayList<Image>,
                   private val itemClick: (Int) -> Unit) : RecyclerView.Adapter<OrderAdapter.ViewHolder>() {
    override fun getItemCount(): Int {
        return selectedImages.size
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.order_item, parent, false)
        return ViewHolder(view, this)
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        holder?.bindImage(selectedImages[position])
    }

    inner class ViewHolder(view: View,
                           private val orderAdapter: OrderAdapter) : RecyclerView.ViewHolder(view) {
        fun bindImage(image: Image) {
            Glide.with(context).load(image.path).centerCrop().into(itemView.orderItemPhotoIv)

            itemView.orderItemRemoveButtonIv.setOnClickListener {
                selectedImages.remove(image)
                orderAdapter.notifyDataSetChanged()
            }

            itemView.orderItemPlaceholderIv.setOnClickListener {
                itemClick(selectedImages.indexOf(image))
            }
        }
    }
}
