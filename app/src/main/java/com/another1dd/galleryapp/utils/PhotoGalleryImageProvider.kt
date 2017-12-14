package com.another1dd.galleryapp.utils

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import com.another1dd.galleryapp.models.Image


object PhotoGalleryImageProvider {
    @SuppressLint("Recycle")
    fun getAllShownImagesPath(context: Context): ArrayList<Image> {
        val uri: Uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val cursor: Cursor
        val columnIndexData: Int
        val listOfAllImages = ArrayList<Image>()

        val projection = arrayOf(MediaStore.MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME)

        cursor = context.contentResolver.query(uri, projection, null, null, null)
        columnIndexData = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)

        while (cursor.moveToNext()) {
            val path = cursor.getString(columnIndexData)
            val id = cursor.getLong(cursor.getColumnIndex(projection[0]))
            val name = cursor.getString(cursor.getColumnIndex(projection[1]))
            listOfAllImages.add(Image(id, name, path, false))
        }
        return listOfAllImages
    }
}