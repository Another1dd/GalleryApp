package com.another1dd.galleryapp.ui.activities

import android.annotation.SuppressLint
import android.app.FragmentTransaction
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.another1dd.galleryapp.R
import com.another1dd.galleryapp.models.Image
import com.another1dd.galleryapp.models.constants.GalleryType
import com.another1dd.galleryapp.ui.dialogs.InstagramAuthenticationDialog
import com.another1dd.galleryapp.ui.fragments.GalleryFragment
import com.another1dd.galleryapp.ui.fragments.MainFragment
import com.another1dd.galleryapp.utils.insta.InstagramAuthenticationListener
import com.another1dd.galleryapp.utils.rx.RxArrayList
import com.facebook.CallbackManager


class MainActivity : AppCompatActivity(), InstagramAuthenticationListener {
    private lateinit var fragmentTranaction: FragmentTransaction
    internal val selectedImages = RxArrayList<Image>()
    internal var instagramAccessToken: String? = null

    internal var callbackManager: CallbackManager? = null

    private lateinit var instagramAuthenticationDialog: InstagramAuthenticationDialog

    @SuppressLint("CommitTransaction")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        callbackManager = CallbackManager.Factory.create()


        fragmentTranaction = fragmentManager.beginTransaction()
        fragmentTranaction.add(R.id.fragmentContainer, MainFragment()).addToBackStack("main").commit()
    }

    override fun onBackPressed() {
        val fragmentManager = fragmentManager
        if (fragmentManager.backStackEntryCount > 1) {
            fragmentManager.popBackStack()
        } else {
            finish()
        }
    }

    fun getInstagramToken() {
        instagramAuthenticationDialog = InstagramAuthenticationDialog(this@MainActivity, this@MainActivity)
        instagramAuthenticationDialog.setCancelable(true)
        instagramAuthenticationDialog.show()
    }

    override fun onInstagramCodeReceived(accessToken: String?) {
        if (accessToken == null) {
            instagramAuthenticationDialog.dismiss()
        }

        Log.d("AccessToken", accessToken)
        this.instagramAccessToken = accessToken

        startGalleryFragment(GalleryType.INSTAGRAM)
    }

    private fun startGalleryFragment(type: Int) {
        val galleryFragment = GalleryFragment()
        val bundle = Bundle()
        bundle.putInt(GalleryType.TYPE, type)
        galleryFragment.arguments = bundle
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentContainer, galleryFragment).addToBackStack("gallery").commit()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager?.onActivityResult(requestCode, resultCode, data)
    }
}
