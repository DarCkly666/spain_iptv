package com.funapps.spainiptv.model

import java.io.Serializable

class TVResponse(val license: License, val epg: Epg, val countries: ArrayList<Country>) : Serializable{
}