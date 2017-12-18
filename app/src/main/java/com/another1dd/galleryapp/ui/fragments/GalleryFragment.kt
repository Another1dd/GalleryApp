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
import com.another1dd.galleryapp.extensions.gone
import com.another1dd.galleryapp.extensions.inflate
import com.another1dd.galleryapp.extensions.visible
import com.another1dd.galleryapp.models.Image
import com.another1dd.galleryapp.models.beans.instagram.InstagramResponse
import com.another1dd.galleryapp.models.constants.GalleryType
import com.another1dd.galleryapp.rest.InstagramRestAPI
import com.another1dd.galleryapp.rest.RestClient
import com.another1dd.galleryapp.ui.activities.MainActivity
import com.another1dd.galleryapp.ui.adapters.gallery.GalleryAdapter
import com.another1dd.galleryapp.ui.adapters.gallery.GridSpacingItemDecoration
import com.another1dd.galleryapp.utils.coroutines.Android
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_gallery.*
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import ru.gildor.coroutines.retrofit.Result
import ru.gildor.coroutines.retrofit.awaitResult


class GalleryFragment : Fragment() {
    private lateinit var galleryAdapter: GalleryAdapter
    private lateinit var images: ArrayList<Image>
    private var selectedImagesDisposable: Disposable? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return container?.inflate(R.layout.fragment_gallery)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerView()
        initButtons()
    }

    private fun initRecyclerView() {
        val gridLayoutManager = GridLayoutManager(activity, 3, GridLayoutManager.VERTICAL, false)
        galleryFragmentRecyclerView.layoutManager = gridLayoutManager
        galleryFragmentRecyclerView.addItemDecoration(GridSpacingItemDecoration(3, 10, false))

        val bundle = this.arguments
        if (bundle != null) {
            when (bundle.get(GalleryType.TYPE)) {
                GalleryType.GALLERY -> fillRVWithExternalStorageImages()
                GalleryType.INSTAGRAM -> fillRVWithInstagramImages()
            }
        }
    }

    private fun fillRVWithExternalStorageImages() {
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

    private fun initButtons() {
        galleryFragmentNextButton.setOnClickListener {
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.fragmentContainer, OrderFragment()).addToBackStack("order").commit()
        }
    }

    override fun onResume() {
        super.onResume()
        selectedImagesDisposable = (activity as MainActivity).selectedImages.asObservable()
                .observeOn(AndroidSchedulers.mainThread()).subscribe {
            if ((activity as MainActivity).selectedImages.size > 0) {
                galleryFragmentNextButton.visible()
            } else {
                galleryFragmentNextButton.gone()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        selectedImagesDisposable?.dispose()
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

    private fun fillRVWithInstagramImages() {
        var images: ArrayList<Image>? = null
        val accessToken = (activity as MainActivity).instagramAccessToken
        if (accessToken != null) {
            val request = async {
                val api = InstagramRestAPI(RestClient.retrofitService)
                val result = api.getPhotos(accessToken, 20).awaitResult()
                images = when (result) {
                    is Result.Ok -> process(result.value)
                    is Result.Error -> throw Throwable("HTTP error: ${result.response.message()}")
                    is Result.Exception -> throw result.exception
                    else -> {
                        throw Throwable("Something went wrong, please try again later.")
                    }
                }
            }
            launch(Android) {
                request.await()


                if (images != null) {
                    val iterator = (activity as MainActivity).selectedImages.iterator()
                    while (iterator.hasNext()) {
                        if (!images!!.contains(iterator.next())) {
                            iterator.remove()
                        }
                    }

                    galleryAdapter = GalleryAdapter(activity, images!!, (activity as MainActivity).selectedImages)
                    galleryFragmentRecyclerView.adapter = galleryAdapter
                }
            }
        }
    }

    private fun process(response: InstagramResponse): ArrayList<Image>? {
        val dataResponse = response.data
        val images = ArrayList<Image>()
        dataResponse?.forEach {

            val image = it.user?.fullName?.let { it1 ->
                it.images?.standardResolution?.url?.let { it2 ->
                    Image(0L, it1,
                            it2, true)
                }
            }
            if (!(activity as MainActivity).selectedImages.contains(image)) {
                image?.isSelected = false
            }
            image?.let { it1 -> images.add(it1) }
        }
        return images
    }
}
