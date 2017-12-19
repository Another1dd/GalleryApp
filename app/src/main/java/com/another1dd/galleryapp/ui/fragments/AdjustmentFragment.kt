package com.another1dd.galleryapp.ui.fragments


import android.app.Fragment
import android.net.Uri
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.another1dd.galleryapp.R
import com.another1dd.galleryapp.extensions.inflate
import com.another1dd.galleryapp.ui.activities.MainActivity
import com.another1dd.galleryapp.ui.adapters.adjustment.AdjustmentAdapter
import com.another1dd.galleryapp.ui.adapters.adjustment.AdjustmentItemDecorator
import com.another1dd.galleryapp.ui.adapters.order.LinearLayoutManagerWrapper
import com.another1dd.galleryapp.ui.views.TransformImageView
import kotlinx.android.synthetic.main.crop_view.*
import kotlinx.android.synthetic.main.fragment_adjustment.*
import java.io.File
import java.lang.Exception


class AdjustmentFragment : Fragment(), TransformImageView.TransformImageListener {
    private var index = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return container?.inflate(R.layout.fragment_adjustment)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerView()
        initCropView()
    }

    private fun initRecyclerView() {
        val linearLayoutManager = LinearLayoutManagerWrapper(activity, LinearLayoutManager.HORIZONTAL, false)
        adjustmentRecyclerView.layoutManager = linearLayoutManager

        val adjustmentAdapter = AdjustmentAdapter(activity, (activity as MainActivity).selectedImages, index, { index ->
            //            Glide.with(this@AdjustmentFragment.activity).load((activity as MainActivity).selectedImages[index].path)
//                    .centerCrop().into(adjustmentRedactorIv)
            val uri = Uri.fromFile(File((activity as MainActivity).selectedImages[index].path))
            imageViewCrop.setImageUri(uri, uri)
        })
        adjustmentRecyclerView.adapter = adjustmentAdapter
        adjustmentRecyclerView.addItemDecoration(AdjustmentItemDecorator(25))
    }

    private fun initCropView() {
        imageViewCrop.isScaleEnabled = true
        imageViewCrop.setImageToWrapCropBounds(true)
        imageViewCrop.setTransformImageListener(this)

        val bundle = this.arguments
        if (bundle != null) {
            index = bundle.getInt("index")
//            Glide.with(activity).load((activity as MainActivity).selectedImages[index].path)
//                    .centerCrop().into(adjustmentRedactorIv)
            val uri = Uri.fromFile(File((activity as MainActivity).selectedImages[index].path))
            imageViewCrop.setImageUri(uri, uri)
        }
    }


    override fun onLoadComplete() {
        changeCropType(1f / 1f)
    }

    override fun onScale(currentScale: Float) {

    }

    override fun onLoadFailure(e: Exception) {

    }

    private fun changeCropType(crop: Float) {
        viewOverlay.setTargetAspectRatio(crop)
        imageViewCrop.targetAspectRatio = crop
    }
}
