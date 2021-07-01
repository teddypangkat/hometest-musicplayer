package com.telkomsel.hometestmusicplayer.ui.main.adapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.telkomsel.hometestmusicplayer.R
import com.telkomsel.hometestmusicplayer.data.model.MusicModel
import com.telkomsel.hometestmusicplayer.data.model.MusicModelResult

class MusicAdapter(){


    class ItemMusicHolder(view: View) : RecyclerView.ViewHolder(view) {

        var itemTitle: TextView = view.findViewById(R.id.item_title)
        var itemAlbum: TextView = view.findViewById(R.id.item_album)
        var itemArtist: TextView = view.findViewById(R.id.item_artist)
        var itemAlbumArt: ImageView = view.findViewById(R.id.item_art_album)

        fun bindItem(modelData: MusicModelResult) {
            itemTitle.text = modelData.trackName
            itemAlbum.text = modelData.collectionName
            itemArtist.text = modelData.artistName
        }
    }


}