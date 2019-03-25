package com.example.cbr.webview

import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.webkit.*
import android.widget.Toast
import com.example.cbr.webview.utility.ChromeClient

import com.example.cbr.webview.utility.ConstantClass
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import kotlinx.android.synthetic.main.activity_main.*
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import android.content.Intent
import com.facebook.*
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.facebook.login.LoginResult
import org.json.JSONException
import java.io.UnsupportedEncodingException
import java.net.URL
import java.net.URLEncoder
import java.util.*

class MainActivity : AppCompatActivity() {
    companion object {
        var geoOrigin: String? = null
        var geoCallBack: GeolocationPermissions.Callback? = null
    }
    lateinit var callbackManager: CallbackManager
    lateinit var mGoogleSignInClient: GoogleSignInClient
    /*Todo create swipe to pull refresh here*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        facebookLoginInit()
        googleLoginInit()
        WebViewSetup()
    }

    private fun googleLoginInit() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        google.setOnClickListener {
            signIn()
        }
    }

    private fun facebookLoginInit() {
        callbackManager = CallbackManager.Factory.create()
        loginButton.setReadPermissions(Arrays.asList(ConstantClass.email,
                ConstantClass.birthday,ConstantClass.user_friend,ConstantClass.public_profile))
        // Callback registration
        loginButton.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                // App code
                Log.w("URL", "facebook: success")
                RequestData()
            }

            override fun onCancel() {
                // App code
                Log.w("cancel", "facebook: cancel")
            }

            override fun onError(exception: FacebookException) {
                // App code
                Log.w("error", "facebook: error")
            }
        })
    }

    private fun signIn() {
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, 1002)
    }

    fun RequestData() {
        val request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken()) { `object`, response ->
            val imgUrl: URL? = null
            Log.e("response", response.toString())
            val json = response.jsonObject
            try {
                if (json != null) {
                    //                        String text = "<b>Name :</b> " + json.getString("name") + "<br><br><b>Email :</b> " + json.getString("email") + "<br><br><b>Profile link :</b> " + json.getString("link") + json.getString("picture") + "<br><br><b>picture :</b> " + json.getString("picture");
                    //                       Log.i("Detail", "" + Html.fromHtml(text));

                    var facebookEmail = json.getString("email")
                    var name = json.getString("name")
                    try {
                       var  postData = "email=" + URLEncoder.encode(facebookEmail, "UTF-8") +
                                "&fname=" + URLEncoder.encode(name, "UTF-8") +
                                "&lname=" + URLEncoder.encode("", "UTF-8") +
                                "&device_id=" + URLEncoder.encode("1234555", "UTF-8") +
                                "&device_type=" + URLEncoder.encode("android", "UTF-8") +
                                "&device_token=" + URLEncoder.encode(" 12343434AAADD", "UTF-8") +
                                "&sltype=" + URLEncoder.encode("fp", "UTF-8") +
                                "&auth=" + URLEncoder.encode("12345", "UTF-8") +
                                "&ref=" + URLEncoder.encode("", "UTF-8")
                        //webview.loadUrl("http://idigities.com/demo/woodofa2/sociallogin/setup?email="+facebookEmail+"&fname="+name+"&lname=&device_id=12344&device_type=android&device_token=53454GDFDFDFDFdsdfk4544&sltype=fp&ref"+after_ref);
//                        webview.postUrl("http://woodofa.com/woodofa_app//sociallogin/setup", postData.toByteArray())
                    } catch (e: UnsupportedEncodingException) {
                        e.printStackTrace()
                    }
                }

            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == 1002) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)

            // Signed in successfully, show authenticated UI.
            Log.w("URL", "d: " + account!!.email)
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("URL ", "signInResult:failed code=" + e.statusCode)
            /*TOdo when seeing this error code, there is nothing user can do to recover from the sign in failure.
            Switching to another account may or may not help. Check adb log to see details if any.*/

//            updateUI(null)
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            ConstantClass.fineLocationPermissionReqcode -> {
                if (permissions.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (geoCallBack != null) {
                        geoCallBack!!.invoke(geoOrigin, true, true)
                    }
                } else {
                    if (geoCallBack != null) {
                        geoCallBack!!.invoke(geoOrigin, false, true)
                    }
                }
            }
        }
    }

    override fun onBackPressed() {
        if (webview != null) {
            if (webview.canGoBack())
                webview.goBack()
            else
                finish()
        } else
            super.onBackPressed()
    }

    fun WebViewSetup() {
        webview.settings.javaScriptEnabled = true
        webview.settings.domStorageEnabled = true
        webview.settings.setGeolocationEnabled(true)
        webview.webChromeClient = ChromeClient(this@MainActivity)
        webview.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {

                return false
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                Log.w("URL", "r: " + url)
//                Toast.makeText(this@MainActivity, "finished", Toast.LENGTH_SHORT).show()
            }


            override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                super.onReceivedError(view, request, error)
                Toast.makeText(this@MainActivity, "error", Toast.LENGTH_SHORT).show()
                /*Todo inflate error layout*/
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                Toast.makeText(this@MainActivity, "started", Toast.LENGTH_SHORT).show()
            }
        }
        webview.loadUrl(ConstantClass.base_url)
    }

    /*
    *  WebView webview = new WebView(this);
    *  setContentView(webview);
    *  String url = "http://www.example.com";
    *  String postData = "username=" + URLEncoder.encode(my_username, "UTF-8") + "&password=" + URLEncoder.encode(my_password, "UTF-8");
    *  webview.postUrl(url,postData.getBytes());
    */

}
