package com.funapps.spainiptv.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.funapps.spainiptv.R
import com.funapps.spainiptv.model.Channel
import com.funapps.spainiptv.player.PlayerActivity
import com.funapps.spainiptv.services.RadioPlayerService
import com.google.android.exoplayer2.Player
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

class ChannelAdapterGrid(private val mContext: Activity, private val channels: ArrayList<Channel>): RecyclerView.Adapter<ChannelAdapterGrid.MyViewHolder>() {

    private var mInterstitialAd: InterstitialAd? = null
    init {
        loadInterstitial()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChannelAdapterGrid.MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_channel_grid, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChannelAdapterGrid.MyViewHolder, position: Int) {
        val channel = channels[position]
        holder.name.text = channel.name
        Glide.with(mContext).load(channel.logo).placeholder(R.mipmap.ic_launcher_foreground).into(holder.image)
        holder.card.setOnClickListener {
            mContext.stopService(Intent(mContext, RadioPlayerService::class.java))
            if (channel.options.size != 0){
                mInterstitialAd?.show(mContext)
                val intent: Intent = Intent(mContext, PlayerActivity::class.java)
                intent.putExtra("URL", channel.options[0].url)
                intent.putExtra("IMAGE", channel.logo)
                intent.putExtra("NAME", channel.name)
                mContext.startActivity(intent)
            }else{
                Toast.makeText(mContext, "No hay fuente disponible", Toast.LENGTH_SHORT).show()
            }

        }
    }

    override fun getItemCount(): Int {
        return channels.size
    }

    private fun loadInterstitial(){
        val adRequest = AdRequest.Builder().build()

        InterstitialAd.load(mContext,mContext.getString(R.string.idinterstitial), adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                mInterstitialAd = null
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                mInterstitialAd = interstitialAd
                showInterstitial()
            }
        })
    }

    private fun showInterstitial() {
        if (mInterstitialAd != null) {
            mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    Log.d("INTERSTITIALADMOB", "Ad was dismissed.")
                    // Don't forget to set the ad reference to null so you
                    // don't show the ad a second time.
                    mInterstitialAd = null
                    loadInterstitial()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError?) {
                    Log.d("INTERSTITIALADMOB", "Ad failed to show.")
                    // Don't forget to set the ad reference to null so you
                    // don't show the ad a second time.
                    mInterstitialAd = null
                }

                override fun onAdShowedFullScreenContent() {
                    Log.d("INTERSTITIALADMOB", "Ad showed fullscreen content.")
                    // Called when ad is dismissed.
                }
            }
            //mInterstitialAd?.show(requireActivity())
        }
    }

    class MyViewHolder(view: View):RecyclerView.ViewHolder(view){
        val card: CardView = view.findViewById(R.id.item_channel_card)
        val image: ImageView = view.findViewById(R.id.item_channel_image)
        val name: TextView = view.findViewById(R.id.item_channel_name)
    }
}