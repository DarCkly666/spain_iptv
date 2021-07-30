package com.funapps.spainiptv.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.session.PlaybackState
import android.os.Build
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.media.session.MediaButtonReceiver
import com.funapps.spainiptv.MainActivity
import com.funapps.spainiptv.R
import com.funapps.spainiptv.SplashActivity
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.MediaMetadata
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import java.io.IOException
import java.net.URL


//https://www.sitepoint.com/a-step-by-step-guide-to-building-an-android-audio-player-app/

private const val TAG = "PlayerActivity"
private const val URLRADIO = "https://playerservices.streamtheworld.com/api/livestream-redirect/RADIOLE.mp3"
//private const val URLRADIO = "https://rtvelivestreamv3.akamaized.net/rne_r3_main.m3u8"
//private const val URLRADIO = "http://s02.fjperezdj.com:8050/live"
//private const val URLRADIO = "http://rac105.radiocat.net/;*.nsv"

class RadioPlayerService: Service(), Player.EventListener {


    private var player: SimpleExoPlayer? = null

    private var playWhenReady = true
    private var currentWindow = 0

    private var isPlaying:Boolean = false

    private lateinit var mediaSession: MediaSessionCompat

    private lateinit var notification: NotificationCompat.Builder
    private var radioName = "Radios España"
    private var radioImage: String = ""

    private fun playRadio(url: String){
        if (isPlaying){
            releasePlayer()
            isPlaying = false
        }else{
            player = SimpleExoPlayer.Builder(this).build()
            val mediaItem = MediaItem.fromUri(url)
            mediaSession = MediaSessionCompat(this, "PLAYER")
            val mediaSessionConnector = MediaSessionConnector(mediaSession)
            player!!.setMediaItem(mediaItem)
            player!!.playWhenReady = playWhenReady
            player!!.prepare()
            isPlaying = true
            mediaSessionConnector.setPlayer(player!!)
            //createNotification()
            createNotificationDoc()
            player!!.addListener(this)
        }
    }


    private fun releasePlayer() {
        player?.run {
            currentWindow = this.currentWindowIndex
            playWhenReady = this.playWhenReady
            release()
            mediaSession.isActive = false
            mediaSession.release()
        }
        player = null
    }

    private fun getImage(radioUrl: String){
        var image: Bitmap? = null
        Log.d("IMAGEGET-NULL", image.toString())
        try {
            val thread = Thread(){
                val url = URL(radioUrl)
                image = BitmapFactory.decodeStream(url.openConnection().getInputStream())
                Log.d("IMAGEGET-THREAD", image.toString())
                notification.setLargeIcon(image)
                with(NotificationManagerCompat.from(this)){
                    notify(1001, notification.build())
                }
            }
            thread.start()
            Log.d("IMAGEGET-TRY", image.toString())
        } catch (e: IOException) {
            Log.e("ERROR", e.message.toString())
            image = BitmapFactory.decodeResource(
                applicationContext.resources,
                R.drawable.notification_icon
            )
            Log.d("IMAGEGET-CATCH", image.toString())
        }finally {
            notification.setLargeIcon(image)
            Log.d("IMAGEGET-FINALLY", image.toString())
        }
    }

    private fun createNotificationDoc(){
        // Given a media session and its context (usually the component containing the session)
        // Create a NotificationCompat.Builder

        // Get the session's metadata
        val controller = mediaSession.controller
        val mediaMetadata = controller.metadata
        val channelId = "com.funapps.spainiptv"

        val icon = BitmapFactory.decodeResource(
            applicationContext.resources,
            R.drawable.notification_icon
        )

        createNotificationChannel(channelId)
        val intent = Intent(this, StopRadioService::class.java)
        val stopPendingIntent: PendingIntent = PendingIntent.getBroadcast(this, StopRadioService.REQUEST_CODE, intent, PendingIntent.FLAG_CANCEL_CURRENT)

        val contentIntent = Intent(this, MainActivity::class.java).apply {
            addCategory(Intent.ACTION_MAIN)
            addCategory(Intent.CATEGORY_LAUNCHER)
            addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT)
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }
        //val contentIntent = packageName.laun
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, contentIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        notification = NotificationCompat.Builder(this, channelId).apply {
            // Add the metadata for the currently playing track
            setContentTitle(radioName)
            setContentText(radioName)
            setLargeIcon(icon)
            setContentIntent(pendingIntent)

            // Enable launching the player by clicking the notification
            //setContentIntent(controller.sessionActivity)

            // Stop the service when the notification is swiped away
            setDeleteIntent(
                MediaButtonReceiver.buildMediaButtonPendingIntent(
                    this@RadioPlayerService,
                    PlaybackStateCompat.ACTION_STOP
                )
            )

            // Make the transport controls visible on the lockscreen
            setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

            // Add an app icon and set its accent color
            // Be careful about the color
            setSmallIcon(R.drawable.ic_radio)

            // Add a pause button
            addAction(
                NotificationCompat.Action(
                    R.drawable.ic_stop_24,
                    "Stop",
                    stopPendingIntent
                )
            )

            val style = androidx.media.app.NotificationCompat.MediaStyle().setMediaSession(mediaSession.sessionToken)
            style.setShowActionsInCompactView(0)
                .setShowCancelButton(true)
                .setCancelButtonIntent(
                    stopPendingIntent
                )
            // Take advantage of MediaStyle features
            setStyle(style)
        }

        // Display the notification and place the service in the foreground
        startForeground(1001, notification.build())

    }

    private fun createNotificationChannel(channelId: String) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.app_name)
            val descriptionText = "channel description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
        if (mediaMetadata.title != null) {
            Log.d("METADATA", "Album: ${mediaMetadata.albumArtist}\nAlbum Title: ${mediaMetadata.albumTitle}" +
                "\nArtist: ${mediaMetadata.artist}\nTitle: ${mediaMetadata.title}")
            notification.setContentText(mediaMetadata.title)

            with(NotificationManagerCompat.from(this)){
                notify(1001, notification.build())
            }
        }
    }

    override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
        Log.d("MEDIAPLAYER", "Play When Ready: $playWhenReady")
    }

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        Log.d("MEDIAPLAYER", "Player state changed: $playWhenReady")
    }

    override fun onPlaybackStateChanged(state: Int) {
        Log.d("MEDIAPLAYER", "playbackstatechanged: $state")
        if (PlaybackState.STATE_BUFFERING == state){
            Log.d("STATEPLAYER", "buffering $state")
        }
        if (PlaybackState.STATE_PLAYING == state){
            Log.d("STATEPLAYER", "playing $state")
        }
        if (PlaybackState.STATE_ERROR == state){
            Log.d("STATEPLAYER", "error $state")
            isPlaying = false
        }
        if (PlaybackState.STATE_CONNECTING == state){
            Log.d("STATEPLAYER", "connecting $state")
        }
        if (PlaybackState.STATE_STOPPED == state){
            Log.d("STATEPLAYER", "stopped $state")
            releasePlayer()
            isPlaying = false
        }
        if (PlaybackState.STATE_PAUSED == state){
            Log.d("STATEPLAYER", "paused $state")
            releasePlayer()
            isPlaying = false
        }
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        Log.d("MEDIAPLAYER", "is Playing Changed: $isPlaying")

    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val url = intent?.getStringExtra("RADIOURL").toString()
        radioName = intent?.getStringExtra("RADIONAME").toString()
        radioImage = intent?.getStringExtra("RADIOIMAGE").toString()

        if (player == null) {
            playRadio(url)
        }
        mediaSession.isActive = true

        getImage(radioImage)
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        releasePlayer()
    }
}
