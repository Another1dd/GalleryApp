package com.another1dd.galleryapp.ui.activities

import android.annotation.SuppressLint
import android.app.FragmentTransaction
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.another1dd.galleryapp.R
import com.another1dd.galleryapp.ui.fragments.MainFragment


class MainActivity : AppCompatActivity() {
    lateinit var fragmentTranaction: FragmentTransaction


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
