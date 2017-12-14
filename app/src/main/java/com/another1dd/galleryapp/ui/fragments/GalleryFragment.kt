package com.another1dd.galleryapp.ui.fragments


import android.app.Fragment
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.another1dd.galleryapp.R
import com.another1dd.galleryapp.models.Image
import com.another1dd.galleryapp.ui.adapters.GalleryAdapter
import com.another1dd.galleryapp.ui.adapters.GridSpacingItemDecoration
import com.another1dd.galleryapp.utils.PhotoGalleryImageProvider
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
            galleryAdapter = GalleryAdapter(activity, images)
            galleryFragmentRecyclerView.adapter = galleryAdapter
        }
    }

    private suspend fun getImagesForGallery(): ArrayList<Image> {
        return PhotoGalleryImageProvider.getAllShownImagesPath(activity)
    }
}
