package com.another1dd.galleryapp.ui.fragments


import android.app.Fragment
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.another1dd.galleryapp.R
import com.another1dd.galleryapp.extensions.inflate
import com.another1dd.galleryapp.models.constants.DivideType
import com.another1dd.galleryapp.ui.activities.MainActivity
import com.another1dd.galleryapp.ui.adapters.adjustment.AdjustmentAdapter
import com.another1dd.galleryapp.ui.adapters.adjustment.AdjustmentItemDecorator
import com.another1dd.galleryapp.ui.adapters.order.LinearLayoutManagerWrapper
import com.another1dd.galleryapp.ui.views.TransformImageView
import kotlinx.android.synthetic.main.fragment_adjustment.*
import kotlinx.android.synthetic.main.redactor_view.*


class AdjustmentFragment : Fragment(), TransformImageView.TransformImageListener {
    private var index = 0
    private var adjustmentAdapter: AdjustmentAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return container?.inflate(R.layout.fragment_adjustment)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initCropView()
        initRecyclerView()
        initButtons()
    }

    private fun initRecyclerView() {
        val linearLayoutManager = LinearLayoutManagerWrapper(activity, LinearLayoutManager.HORIZONTAL, false)
        adjustmentRecyclerView.layoutManager = linearLayoutManager

        adjustmentAdapter = AdjustmentAdapter(activity, (activity as MainActivity).selectedImages, index, { index ->
            this.index = index
            val image = (activity as MainActivity).selectedImages[index]
            viewOverlay.setDividerType(image.divideType)
            adjustmentRedactorView.resetCropImageView(image.path)
        })
        adjustmentRecyclerView.adapter = adjustmentAdapter
        adjustmentRecyclerView.addItemDecoration(AdjustmentItemDecorator(25))
    }

    private fun initCropView() {
        imageViewCrop.setImageToWrapCropBounds(true)
        imageViewCrop.setTransformImageListener(this)

        val bundle = this.arguments
        if (bundle != null) {
            index = bundle.getInt("index")
            val image = (activity as MainActivity).selectedImages[index]
            imageViewCrop.setImageUri(image.path)
            viewOverlay.setDividerType(image.divideType)
        }
    }

    private fun initButtons() {
        adjustmentButtonOneXOne.setOnClickListener {
            viewOverlay.setDividerType(DivideType.NO_DIVIDE)
            (activity as MainActivity).selectedImages[index].divideType = DivideType.NO_DIVIDE
            adjustmentAdapter?.notifyItemChanged(index)
        }

        adjustmentButtonThreeXThree.setOnClickListener {
            viewOverlay.setDividerType(DivideType.NINE_DIVIDE)
            (activity as MainActivity).selectedImages[index].divideType = DivideType.NINE_DIVIDE
            adjustmentAdapter?.notifyItemChanged(index)
        }

        adjustmentButtonOneXThreeCenter.setOnClickListener {
            viewOverlay.setDividerType(DivideType.HORIZONTAL_CENTER_DIVIDE)
            (activity as MainActivity).selectedImages[index].divideType = DivideType.HORIZONTAL_CENTER_DIVIDE
            adjustmentAdapter?.notifyItemChanged(index)
        }

        adjustmentButtonOneXThreeTop.setOnClickListener {
            viewOverlay.setDividerType(DivideType.HORIZONTAL_TOP_DIVIDE)
            (activity as MainActivity).selectedImages[index].divideType = DivideType.HORIZONTAL_TOP_DIVIDE
            adjustmentAdapter?.notifyItemChanged(index)
        }

        adjustmentButtonOneXThreeBot.setOnClickListener {
            viewOverlay.setDividerType(DivideType.HORIZONTAL_BOT_DIVIDE)
            (activity as MainActivity).selectedImages[index].divideType = DivideType.HORIZONTAL_BOT_DIVIDE
            adjustmentAdapter?.notifyItemChanged(index)
        }

        adjustmentButtonThreeXOneCenter.setOnClickListener {
            viewOverlay.setDividerType(DivideType.VERTICAL_CENTER_DIVIDE)
            (activity as MainActivity).selectedImages[index].divideType = DivideType.VERTICAL_CENTER_DIVIDE
            adjustmentAdapter?.notifyItemChanged(index)
        }

        adjustmentButtonThreeXOneLeft.setOnClickListener {
            viewOverlay.setDividerType(DivideType.VERTICAL_LEFT_DIVIDE)
            (activity as MainActivity).selectedImages[index].divideType = DivideType.VERTICAL_LEFT_DIVIDE
            adjustmentAdapter?.notifyItemChanged(index)
        }

        adjustmentButtonThreeXOneRight.setOnClickListener {
            viewOverlay.setDividerType(DivideType.VERTICAL_RIGHT_DIVIDE)
            (activity as MainActivity).selectedImages[index].divideType = DivideType.VERTICAL_RIGHT_DIVIDE
            adjustmentAdapter?.notifyItemChanged(index)
        }
    }


    override fun onLoadComplete() {
        changeCropType(1f / 1f)
    }

    //Scale disabled
    override fun onScale(currentScale: Float) {

    }

    private fun changeCropType(crop: Float) {
        viewOverlay.setTargetAspectRatio(crop)
        imageViewCrop.targetAspectRatio = crop
    }
}
