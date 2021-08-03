package com.funapps.spainiptv

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.funapps.spainiptv.model.TVResponse
import com.funapps.spainiptv.util.CheckConnectivity
import com.funapps.spainiptv.util.SharedPrefs
import com.funapps.spainiptv.services.RadioPlayerService
import com.funapps.spainiptv.util.Constants
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_SpainIPTV_NoActionBar)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        loadLoading()

        val connection: CheckConnectivity = CheckConnectivity(this)
        if (connection.isConnected()) {
            if (isGooglePlayServicesAvailable(this)){
                getConfigs()
            }
        }else{
            Toast.makeText(this, getString(R.string.no_connection), Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun loadLoading(){
        val imageLoading: ImageView = findViewById(R.id.splash_loading)
        Glide.with(this).asGif().load(R.drawable.gif).into(imageLoading)
    }

    private fun getConfigs(){
        val database = Firebase.database
        val reference = database.getReference("configs")
        var developmentStatus: Int = 0
        val prefs: SharedPrefs = SharedPrefs(this)
        reference.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val develop = snapshot.child("development").value
                val url: String = snapshot.child("infoweb").value as String
                val responseURL = snapshot.child("tvURL").value as String
                prefs.saveURL(url)
                try {
                    developmentStatus = Integer.parseInt(develop.toString())
                }catch (e: Exception){
                    e.printStackTrace()
                }
                /*Log.d("CONFIGFIRE", "Dev: $developmentStatus")
                Log.d("CONFIGFIRE", "URL: $url")
                Log.d("CONFIGFIRE", "Response URL: $responseURL")*/
                if (developmentStatus == 1){
                    startInfoActivity()
                }else{
                    val myUrl: String = if(responseURL.isNotEmpty()) responseURL else Constants.URL
                    getResponse(myUrl)
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Log.e("FIREBASE", error.details)
            }
        })
    }

    private fun startInfoActivity(){
        startActivity(Intent(this, InfoActivity::class.java))
        finish()
    }

    private fun getResponse(url: String){
        val shared = SharedPrefs(this)
        val queue = Volley.newRequestQueue(this)
        val jsonObjectRequest:JsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null,
            { response ->
                val gson: Gson = Gson()
                val responseTV = gson.fromJson(response.toString(), TVResponse::class.java)
                shared.saveShared(responseTV)
                startActivity(Intent(this, MainActivity::class.java))
                finish()
                //getResponseRadios(Constants.URLRADIO)
            },
            {
                Log.d("VOLLEY", it.message.toString())
            })
        queue.add(jsonObjectRequest)
    }

    private fun getResponseRadios(url: String){
        val shared = SharedPrefs(this)
        val queue = Volley.newRequestQueue(this)
        val jsonObjectRequest: JsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                val gson: Gson = Gson()
                val responseRadio = gson.fromJson(response.toString(), TVResponse::class.java)
                shared.saveSharedRadio(responseRadio)
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            },
            {
                Log.d("VOLLEY", it.message.toString())
            })
        queue.add(jsonObjectRequest)
    }

    private fun isGooglePlayServicesAvailable(activity: Activity): Boolean {
        val googleApiAvailability: GoogleApiAvailability = GoogleApiAvailability.getInstance()
        val status: Int = googleApiAvailability.isGooglePlayServicesAvailable(activity)
        if (status != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(status)) {
                val myDialog = AlertDialog.Builder(this)
                myDialog.setCancelable(false)
                myDialog.setTitle(getString(R.string.play_services))
                myDialog.setMessage(getString(R.string.play_services_message))
                myDialog.setPositiveButton(getString(R.string.play_services_ok)
                ) { dialog, _ ->
                    dialog.dismiss()
                    finish()}
                    /*val handler = Handler()
                    handler.postDelayed({ finish() }, 2000)}*/
                myDialog.show()
                //googleApiAvailability.getErrorDialog(activity, status, 2404)?.show()
            }
            return false
        }
        return true
    }

}