package com.funapps.spainiptv.util

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.funapps.spainiptv.model.TVResponse
import com.google.gson.Gson

class SharedPrefs (private val mContext: Context) {
    fun saveShared(response: TVResponse){
        val shared: SharedPreferences = mContext.getSharedPreferences("RESPONSE", MODE_PRIVATE)
        val editor = shared.edit()
        val gson: Gson = Gson()
        val json: String = gson.toJson(response)
        editor.putString("RESPONSE", json)
        editor.apply()
    }

    fun getShared(): TVResponse{
        val shared: SharedPreferences = mContext.getSharedPreferences("RESPONSE", MODE_PRIVATE)
        val gson: Gson = Gson()
        val json = shared.getString("RESPONSE", "")
        return gson.fromJson(json, TVResponse::class.java)
    }

    fun saveSharedRadio(response: TVResponse){
        val shared: SharedPreferences = mContext.getSharedPreferences("RESPONSERADIO", MODE_PRIVATE)
        val editor = shared.edit()
        val gson: Gson = Gson()
        val json: String = gson.toJson(response)
        editor.putString("RESPONSERADIO", json)
        editor.apply()
    }

    fun getSharedRadio(): TVResponse{
        val shared: SharedPreferences = mContext.getSharedPreferences("RESPONSERADIO", MODE_PRIVATE)
        val gson: Gson = Gson()
        val json = shared.getString("RESPONSERADIO", "")
        return gson.fromJson(json, TVResponse::class.java)
    }
}