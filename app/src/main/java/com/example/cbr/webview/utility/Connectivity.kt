package com.example.cbr.webview.utility

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo

class Connectivity(ctx:Context) {
    var context:Context=ctx
    fun getNetworkInfo():NetworkInfo{
        var connectvityManager: ConnectivityManager= context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return connectvityManager.activeNetworkInfo
    }
    fun getConnectivityStatus():Boolean=getNetworkInfo().isConnected
}