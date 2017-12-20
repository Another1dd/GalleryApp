package com.another1dd.galleryapp.ui.adapters.adjustment

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.another1dd.galleryapp.R
import com.another1dd.galleryapp.extensions.gone
import com.another1dd.galleryapp.extensions.visible
import com.another1dd.galleryapp.models.Image
import com.another1dd.galleryapp.models.constants.DivideType
import com.another1dd.galleryapp.utils.rx.RxArrayList
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.adjustment_item.view.*

class AdjustmentAdapter(private val context: Context,
                        private val selectedImages: RxArrayList<Image>,
                        private var selectedImageIndex: Int,
                        private val itemClick: (Int) -> Unit) : RecyclerView.Adapter<AdjustmentAdapter.ViewHolder>() {
    override fun getItemCount(): Int {
        return selectedImages.size
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.adjustment_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        holder?.bindImage(selectedImages[position])
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bindImage(image: Image) {
            Glide.with(context).load(image.path).centerCrop().into(itemView.adjustmentPhotoIv)

            if (selectedImages.indexOf(image) == selectedImageIndex) {
                itemView.adjustmentSelectedBackIv.visible()
            } else {
                itemView.adjustmentSelectedBackIv.gone()
            }

            itemView.adjustmentPhotoIv.setOnClickListener {
                if (selectedImages.indexOf(image) != selectedImageIndex) {
                    val oldSelectedIndex = selectedImageIndex
                    selectedImageIndex = selectedImages.indexOf(image)
                    itemClick(selectedImageIndex)

                    notifyItemChanged(oldSelectedIndex)
                    notifyItemChanged(selectedImageIndex)
                }
            }

            when (image.divideType){
                DivideType.NO_DIVIDE ->{
                    itemView.adjustmentSplitBlock.gone()
                }
                DivideType.NINE_DIVIDE ->{
                    itemView.adjustmentSplitBlock.visible()
                    itemView.adjustmentSplitTv.text = "9"
                }
                else ->{
                    itemView.adjustmentSplitBlock.visible()
                    itemView.adjustmentSplitTv.text = "3"
                }
            }
        }
    }
}
