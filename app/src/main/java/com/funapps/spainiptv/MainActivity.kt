package com.funapps.spainiptv

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.preference.PreferenceManager
import com.funapps.spainiptv.databinding.ActivityMainBinding
import com.funapps.spainiptv.settings.SettingsActivity
import com.funapps.spainiptv.ui.webview.WebViewActivity
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.messaging.FirebaseMessaging


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private var status: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_spain_home,
                R.id.nav_international,
                R.id.nav_spain_radio,
                R.id.nav_international_radio
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        initialize()

        status =
            PreferenceManager.getDefaultSharedPreferences(this).getBoolean("notification", true)
        checkNotif(status)
    }

    private fun initialize(){
        MobileAds.initialize(this) {}
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.action_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                true
            }
            R.id.action_policy -> {
                loadPolicies()
                true
            }
            R.id.action_about -> {
                loadAbout()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun loadPolicies(){
        val intent = Intent(this, WebViewActivity::class.java)
        intent.putExtra("policies", getString(R.string.privacy_policies))
        startActivity(intent)
    }
    private fun loadAbout(){
        val intent = Intent(this, WebViewActivity::class.java)
        intent.putExtra("about", getString(R.string.about))
        startActivity(intent)
    }
    private fun checkNotif(status: Boolean){
        if (status) {
            FirebaseMessaging.getInstance().subscribeToTopic("spain")
        }else{
            FirebaseMessaging.getInstance().unsubscribeFromTopic("spain")
        }
    }

    override fun onResume() {
        super.onResume()
        checkNotif(status)
    }
}