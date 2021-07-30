package com.funapps.spainiptv.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo

class CheckConnectivity(private val mContext: Context) {
    private val cm = mContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private val activeNetwork: NetworkInfo? = cm.activeNetworkInfo

    fun isConnected(): Boolean{
        return activeNetwork?.isConnectedOrConnecting == true
    }
}