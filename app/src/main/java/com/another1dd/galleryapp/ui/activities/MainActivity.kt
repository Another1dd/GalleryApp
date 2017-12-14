package com.another1dd.galleryapp.ui.activities

import android.annotation.SuppressLint
import android.app.FragmentTransaction
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.another1dd.galleryapp.R
import com.another1dd.galleryapp.models.Image
import com.another1dd.galleryapp.ui.fragments.MainFragment
import com.another1dd.galleryapp.utils.RxArrayList


class MainActivity : AppCompatActivity() {
    private lateinit var fragmentTranaction: FragmentTransaction
    internal val selectedImages = RxArrayList<Image>()

    @SuppressLint("CommitTransaction")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fragmentTranaction = fragmentManager.beginTransaction()
        fragmentTranaction.add(R.id.fragmentContainer, MainFragment()).commit()
    }

    override fun onBackPressed() {
        fragmentManager.popBackStackImmediate()
    }
}
