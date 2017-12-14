package com.another1dd.galleryapp.ui.fragments


import android.app.Fragment
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.another1dd.galleryapp.R
import com.another1dd.galleryapp.models.Image
import com.another1dd.galleryapp.ui.activities.MainActivity
import com.another1dd.galleryapp.ui.adapters.GalleryAdapter
import com.another1dd.galleryapp.ui.adapters.GridSpacingItemDecoration
import com.another1dd.galleryapp.utils.coroutines.Android
import kotlinx.android.synthetic.main.fragment_gallery.*
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch


class GalleryFragment : Fragment() {
    private lateinit var galleryAdapter: GalleryAdapter
    private lateinit var images: ArrayList<Image>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_gallery, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val gridLayoutManager = GridLayoutManager(activity, 3, GridLayoutManager.VERTICAL, false)
        galleryFragmentRecyclerView.layoutManager = gridLayoutManager
        galleryFragmentRecyclerView.addItemDecoration(GridSpacingItemDecoration(3, 10, false))

        val getImages = async {
            images = getImagesForGallery()
        }

        launch(Android) {
            getImages.await()

            val iterator = (activity as MainActivity).selectedImages.iterator()
            while (iterator.hasNext()) {
                if (!images.contains(iterator.next())) {
                    iterator.remove()
                }
            }

            galleryAdapter = GalleryAdapter(activity, images, (activity as MainActivity).selectedImages)
            galleryFragmentRecyclerView.adapter = galleryAdapter
        }
    }

    private suspend fun getImagesForGallery(): ArrayList<Image> {
        return getAllShownImagesPath()
    }

    private fun getAllShownImagesPath(): ArrayList<Image> {
        val uri: Uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val cursor: Cursor
        val columnIndexData: Int
        val listOfAllImages = ArrayList<Image>()

        val projection = arrayOf(MediaStore.MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME)

        cursor = activity.contentResolver.query(uri, projection, null, null, null)
        columnIndexData = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)

        while (cursor.moveToNext()) {
            val path = cursor.getString(columnIndexData)
            val id = cursor.getLong(cursor.getColumnIndex(projection[0]))
            val name = cursor.getString(cursor.getColumnIndex(projection[1]))
            val image = Image(id, name, path, true)
            if (!(activity as MainActivity).selectedImages.contains(image)) {
                image.isSelected = false
            }

            listOfAllImages.add(image)
        }

        cursor.close()
        return listOfAllImages
    }
}
