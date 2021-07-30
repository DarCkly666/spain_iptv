package com.funapps.spainiptv.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class StopRadioService: BroadcastReceiver() {

    companion object {
        val REQUEST_CODE = 333
    }
    override fun onReceive(context: Context?, intent: Intent?) {
        val service = Intent(context, RadioPlayerService::class.java)
        context!!.stopService(service)
    }
}