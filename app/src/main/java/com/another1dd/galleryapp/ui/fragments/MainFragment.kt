package com.another1dd.galleryapp.ui.fragments


import android.Manifest
import android.app.Fragment
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.another1dd.galleryapp.R
import com.another1dd.galleryapp.extensions.inflate
import com.another1dd.galleryapp.models.constants.GalleryType
import com.another1dd.galleryapp.ui.activities.MainActivity
import com.facebook.AccessToken
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import kotlinx.android.synthetic.main.fragment_main.*
import java.util.*


class MainFragment : Fragment() {
    companion object {
        const val PERMISSION_REQUEST_CODE = 1
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return container?.inflate(R.layout.fragment_main)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initFb()
        initButtons()
    }

    private fun initButtons() {
        mainFragmentGalleryButton.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkPermission()) {
                    startGalleryFragment(GalleryType.GALLERY)
                } else {
                    requestPermission()
                }
            } else {
                startGalleryFragment(GalleryType.GALLERY)
            }
        }

        mainFragmentInstagramButton.setOnClickListener {
            (activity as MainActivity).getInstagramToken()
        }

        mainFragmentFacebookButton.setOnClickListener {
            if (AccessToken.getCurrentAccessToken() != null) {
                startGalleryFragment(GalleryType.FACEBOOK)
            } else {
                Toast.makeText(activity, "Login with facebook", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun checkPermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        return result == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), PERMISSION_REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>?, grantResults: IntArray) {
        Log.d("MainFragment", "request permission result")
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startGalleryFragment(GalleryType.GALLERY)
                } else {
                    Toast.makeText(activity, "Pls allow Gallery App to access photos, media, and files on your device.",
                            Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun startGalleryFragment(type: Int) {
        val galleryFragment = GalleryFragment()
        val bundle = Bundle()
        bundle.putInt(GalleryType.TYPE, type)
        galleryFragment.arguments = bundle
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentContainer, galleryFragment).addToBackStack("gallery").commit()
    }

    private fun initFb() {
        mainFragmentLoginButton.registerCallback((activity as MainActivity).callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult) {
                Log.d("Facebook", "OnSuccess")
            }

            override fun onCancel() {
                Log.d("Facebook", "OnCancel")
            }

            override fun onError(error: FacebookException?) {
                Log.d("Facebook", "OnError -> " + error.toString())
            }
        })
        mainFragmentLoginButton.setReadPermissions(Arrays.asList("user_photos"))
    }
}
