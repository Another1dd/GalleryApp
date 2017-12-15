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
import kotlinx.android.synthetic.main.fragment_main.*


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

        mainFragmentButton.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkPermission()) {
                    startGalleryFragment()
                } else {
                    requestPermission()
                }
            } else {
                startGalleryFragment()
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
                    startGalleryFragment()
                } else {
                    Toast.makeText(activity, "Pls allow Gallery App to access photos, media, and files on your device.",
                            Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun startGalleryFragment() {
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentContainer, GalleryFragment()).addToBackStack("gallery").commit()
    }
}
