package com.another1dd.galleryapp.ui.fragments


import android.app.Fragment
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
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_adjustment.*


class AdjustmentFragment : Fragment() {
    private var index = 0
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return container?.inflate(R.layout.fragment_adjustment)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bundle = this.arguments
        if (bundle != null) {
            index = bundle.getInt("index")
            Glide.with(activity).load((activity as MainActivity).selectedImages[index].path)
                    .centerCrop().into(adjustmentRedactorIv)
        }

        initRecyclerView()
    }

    private fun initRecyclerView() {
        val linearLayoutManager = LinearLayoutManagerWrapper(activity, LinearLayoutManager.HORIZONTAL, false)
        adjustmentRecyclerView.layoutManager = linearLayoutManager

        val adjustmentAdapter = AdjustmentAdapter(activity, (activity as MainActivity).selectedImages, index, { index ->
            Glide.with(this@AdjustmentFragment.activity).load((activity as MainActivity).selectedImages[index].path)
                    .centerCrop().into(adjustmentRedactorIv)
        })
        adjustmentRecyclerView.adapter = adjustmentAdapter
        adjustmentRecyclerView.addItemDecoration(AdjustmentItemDecorator(25))
    }
}
