package com.funapps.spainiptv.player.radio

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import com.google.android.exoplayer2.BasePlayer
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer

class SinglePlayer(val player: SimpleExoPlayer): Service(), Player.EventListener{
    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

}