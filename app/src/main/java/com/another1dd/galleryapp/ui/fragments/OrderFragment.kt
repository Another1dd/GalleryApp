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
import com.another1dd.galleryapp.ui.adapters.order.OrderAdapter
import kotlinx.android.synthetic.main.fragment_order.*


class OrderFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return container?.inflate(R.layout.fragment_order)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerView()
    }

    private fun initRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        orderFragmentRecyclerView.layoutManager = linearLayoutManager

        val orderAdapter = OrderAdapter(activity, (activity as MainActivity).selectedImages)
        orderFragmentRecyclerView.adapter = orderAdapter
    }
}
