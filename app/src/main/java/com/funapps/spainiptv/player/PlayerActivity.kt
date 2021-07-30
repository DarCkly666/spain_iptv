package com.funapps.spainiptv.player

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.funapps.spainiptv.R
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.util.MimeTypes
import com.google.android.exoplayer2.util.Util


class PlayerActivity : AppCompatActivity() {

    private var player: SimpleExoPlayer? = null
    private lateinit var playerView: PlayerView

    private lateinit var status: TextView

    private var playWhenReady = true
    private var currentWindow = 0
    private var playbackPosition: Long = 0L

    val TAG: String = "ERROR EXOPLAYER"

    private lateinit var channelName: TextView
    private lateinit var channelImage: ImageView

    private var url = ""//"https://rtvelivestreamv3.akamaized.net/la1_main_dvr.m3u8"
    private var image = ""
    private var name = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_SpainIPTV_NoActionBar)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        url = intent.getStringExtra("URL").toString()
        image = intent.getStringExtra("IMAGE").toString()
        name = intent.getStringExtra("NAME").toString()
        initView()
        loadData()
        hideSystemUi()
        //url = intent.getStringExtra("URL").toString()
    }

    private fun initView(){
        playerView = findViewById(R.id.video_view)
        channelName = findViewById(R.id.player_channel_name)
        channelImage = findViewById(R.id.player_channel_image)
    }

    private fun loadData(){
        channelName.text = name
        Glide.with(this).load(image).placeholder(R.drawable.ic_live_tv).error(R.mipmap.ic_launcher_foreground).into(channelImage)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun initializePlayer() {
        //Create a data source factory
        val dataSourceFactory: DataSource.Factory = DefaultHttpDataSourceFactory()

        val trackSelector = DefaultTrackSelector(this).apply {
            setParameters(buildUponParameters().setMaxVideoSizeSd())
        }//Equivalent to setMaxVideoSize(1279, 719).

        val mediaItem: MediaItem = getMediaItem(url)//MediaItem.fromUri(url)

        //Create a HLS media source pointing to a playlist uri
        val hlsMediaSource: HlsMediaSource = HlsMediaSource.Factory(dataSourceFactory).setAllowChunklessPreparation(true).createMediaSource(mediaItem)
        player = SimpleExoPlayer.Builder(this).setTrackSelector(trackSelector).build()
        playerView.player = player

        try {
            player!!.setMediaSource(hlsMediaSource)
            player!!.repeatMode = Player.REPEAT_MODE_ALL
            player!!.playWhenReady = playWhenReady
            player!!.seekTo(currentWindow, playbackPosition)
            player!!.prepare()
        }catch (e: Exception){
            Log.e(TAG, e.message.toString())
        }
    }

    private fun getMediaItem(url: String): MediaItem{
        var mediaItem: MediaItem = MediaItem.fromUri(url)
        try {
            mediaItem = MediaItem.Builder().setUri(url).setMimeType(MimeTypes.APPLICATION_M3U8).build()
            Log.d("MEDIAITEM ITEM", mediaItem.mediaMetadata.title.toString())
        }catch (e: Exception){
            e.printStackTrace()
            Log.d("MEDIAITEM ERROR", e.message.toString())
        }
        return mediaItem
    }

    private fun hideSystemUi() {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                // Hide the nav bar and status bar
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    private fun releasePlayer() {
        if (player != null) {
            playWhenReady = player!!.playWhenReady
            playbackPosition = player!!.currentPosition
            currentWindow = player!!.currentWindowIndex
            player!!.release()
            player = null
        }
    }

    public override fun onStart() {
        super.onStart()
        if (Util.SDK_INT >= 24) {
            initializePlayer()
        }
        hideSystemUi()
    }

    public override fun onResume() {
        super.onResume()
        if ((Util.SDK_INT < 24 || player == null)) {
            initializePlayer()
        }
        hideSystemUi()
    }

    public override fun onPause() {
        super.onPause()
        if (Util.SDK_INT < 24) {
            releasePlayer()
        }
    }


    public override fun onStop() {
        super.onStop()
        if (Util.SDK_INT >= 24) {
            releasePlayer()
        }
    }
}