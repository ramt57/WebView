package com.example.cbr.webview.utility

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.webkit.GeolocationPermissions
import android.webkit.PermissionRequest
import android.webkit.WebChromeClient
import android.widget.Toast
import com.example.cbr.webview.MainActivity

class ChromeClient(ctx:Context) : WebChromeClient() {
    var context:Context=ctx
    override fun onGeolocationPermissionsShowPrompt(origin: String?, callback: GeolocationPermissions.Callback?) {
        Toast.makeText(context,"Location Promt",Toast.LENGTH_SHORT).show()
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            /*Ask Permission*/
            /* No Explaination Nedded*/
            MainActivity.geoOrigin = origin
            MainActivity.geoCallBack = callback
            ActivityCompat.requestPermissions(context as Activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), ConstantClass.fineLocationPermissionReqcode)
        } else {
            callback!!.invoke(origin, true, true)
        }
    }

    override fun onPermissionRequest(request: PermissionRequest?) {
        super.onPermissionRequest(request)
        Toast.makeText(context,"Permission request",Toast.LENGTH_SHORT).show()
    }

    override fun onPermissionRequestCanceled(request: PermissionRequest?) {
        super.onPermissionRequestCanceled(request)
        Toast.makeText(context,"Permission Canceled",Toast.LENGTH_SHORT).show()
    }
}