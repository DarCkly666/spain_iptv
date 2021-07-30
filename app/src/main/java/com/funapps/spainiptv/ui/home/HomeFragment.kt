package com.funapps.spainiptv.ui.home

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.funapps.spainiptv.R
import com.funapps.spainiptv.adapter.ChannelAdapter
import com.funapps.spainiptv.adapter.ChannelAdapterGrid
import com.funapps.spainiptv.adapter.SpinnerAdapter
import com.funapps.spainiptv.model.Channel
import com.funapps.spainiptv.model.Country
import com.funapps.spainiptv.util.SharedPrefs
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

class HomeFragment : Fragment() {

    private lateinit var ambits: ArrayList<String>
    private lateinit var ambitsSpinner: Spinner

    private lateinit var channels: ArrayList<Channel>

    private lateinit var recycler: RecyclerView
    private lateinit var adapter: ChannelAdapter
    private lateinit var adapterGrid: ChannelAdapterGrid
    private lateinit var country: Country

    private val countryIndex = 0

    private lateinit var mAdView : AdView
    private var mInterstitialAd: InterstitialAd? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        ambitsSpinner = view.findViewById(R.id.ambits_spinner)
        recycler = view.findViewById(R.id.recycler_home)
        channels = ArrayList()

        loadBanner(view)
        /*loadInterstitial()
        showInterstitial()*/

        country = getCountry(view.context)

        ambits = getAmbits()

        loadAmbits(view.context, ambits)

    }

    private fun getAmbits(): ArrayList<String>{
        val ambitsList = ArrayList<String>()
        for (i in country.ambits){
            ambitsList.add(i.name)
        }
        return ambitsList
    }

    private fun loadAmbits(context: Context, ambits: ArrayList<String>){
        //ambitsSpinner.adapter = ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, ambits)
        ambitsSpinner.adapter = SpinnerAdapter(requireContext(), R.layout.item_spinner, ambits)
        ambitsSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                Log.d("AMBIT", position.toString())
                loadAdapter(context, country.ambits[position].channels)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                Log.d("AMBIT", "Nothing selected")
            }

        }
    }

    private fun getCountry(context: Context): Country{
        val prefs = SharedPrefs(context)
        val response = prefs.getShared()
        return response.countries[countryIndex]
    }

    private fun loadAdapter(context: Context, channels: ArrayList<Channel>){
        val prefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val type = prefs.getString("viewChannel", "")
        if (type == "grid"){
            adapterGrid = ChannelAdapterGrid(requireActivity(), channels)
            recycler.layoutManager = GridLayoutManager(context, 3, GridLayoutManager.VERTICAL, false)
            recycler.adapter = adapterGrid
        }else {
            adapter = ChannelAdapter(requireActivity(), channels)
            recycler.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            recycler.adapter = adapter
        }
    }

    private fun loadBanner(view: View){
        mAdView = view.findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)
        mAdView.adListener = object : AdListener(){
            override fun onAdClosed() {
                Log.d("ADMONBANNER", "Closed")
            }

            override fun onAdFailedToLoad(p0: LoadAdError) {
                Log.d("ADMONBANNER", p0.message.toString())
            }

        }
    }

}