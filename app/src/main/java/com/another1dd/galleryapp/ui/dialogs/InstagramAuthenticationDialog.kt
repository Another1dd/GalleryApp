package com.another1dd.galleryapp.ui.dialogs


import android.app.Dialog
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import com.another1dd.galleryapp.R
import com.another1dd.galleryapp.models.constants.Instagram
import com.another1dd.galleryapp.utils.insta.InstagramAuthenticationListener
import kotlinx.android.synthetic.main.instagram_auth_dialog.*

class InstagramAuthenticationDialog(private val listenerInstagram: InstagramAuthenticationListener, context: Context) : Dialog(context) {
    companion object {
     const val url = (Instagram.BASE_URL
             + "oauth/authorize/?client_id="
             + Instagram.CLIENT_ID
             + "&redirect_uri="
             + Instagram.REDIRECT_URI
             + "&response_type=token"
             + "&display=touch&scope=public_content")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.instagram_auth_dialog)
        initializeWebView()
    }

    private fun initializeWebView() {
        instagramDialogWebView.loadUrl(url)
        instagramDialogWebView.webViewClient = object : WebViewClient() {

            internal var authComplete = false

            internal lateinit var access_token: String

            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)

                if (url.contains("#access_token=") && !authComplete) {
                    val uri = Uri.parse(url)
                    access_token = uri.encodedFragment
                    // get the whole token after the '=' sign
                    access_token = access_token.substring(access_token.lastIndexOf("=") + 1)
                    Log.i("", "CODE : " + access_token)
                    authComplete = true
                    listenerInstagram.onInstagramCodeReceived(access_token)
                    dismiss()

                } else if (url.contains("?error")) {
                    Toast.makeText(context, "Error Occured", Toast.LENGTH_SHORT).show()
                    dismiss()
                }
            }
        }
    }
}
