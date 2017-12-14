package com.another1dd.galleryapp.utils

import android.support.v7.util.DiffUtil
import com.another1dd.galleryapp.models.Image


class ImagesDiffUtil(private val oldList: List<Image>, private val newList: List<Image>) : DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldImage = oldList[oldItemPosition]
        val newImage = newList[newItemPosition]
        return oldImage.id == newImage.id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldImage = oldList[oldItemPosition]
        val newImage = newList[newItemPosition]
        return oldImage.name == newImage.name && oldImage.path == newImage.path &&
                oldImage.isSelected == newImage.isSelected
    }
}