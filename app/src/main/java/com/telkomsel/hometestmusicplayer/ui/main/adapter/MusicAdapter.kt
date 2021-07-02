package com.telkomsel.hometestmusicplayer.ui.main.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.telkomsel.hometestmusicplayer.R
import com.telkomsel.hometestmusicplayer.data.model.MusicModel
import com.telkomsel.hometestmusicplayer.data.model.MusicModelResult

class MusicAdapter(var onItemClicked: (Int) -> Unit) : RecyclerView.Adapter<MusicAdapter.ItemMusicHolder>(){

    //var musics = arrayListOf<MusicModelResult>()

    var musics : List<MusicModelResult> = ArrayList(0)
        set(value) {
            field = value
            notifyDataSetChanged()
        }


    inner class ItemMusicHolder(view: View, onItemClicked: (Int) -> Unit) : RecyclerView.ViewHolder(view) {

        var itemTitle: TextView = view.findViewById(R.id.item_title)
        var itemAlbum: TextView = view.findViewById(R.id.item_album)
        var itemArtist: TextView = view.findViewById(R.id.item_artist)
        var itemAlbumArt: ImageView = view.findViewById(R.id.item_art_album)
        var itemIndicator: RelativeLayout = view.findViewById(R.id.item_music_indicator)

        init {
            itemView.setOnClickListener {
                onItemClicked(adapterPosition)
            }
        }

        fun bindItem(modelData: MusicModelResult) {
            itemTitle.text = modelData.trackName
            itemAlbum.text = modelData.collectionName
            itemArtist.text = modelData.artistName

            //setting view indicator music
            if (modelData.isPlay) {
                itemIndicator.visibility = View.VISIBLE
            } else {
                itemIndicator.visibility = View.GONE
            }

            Picasso.get().load(modelData.artworkUrl60).into(itemAlbumArt)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemMusicHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_music, parent, false)
        return ItemMusicHolder(view) {
            onItemClicked(it)
        }
    }
    override fun onBindViewHolder(holder: ItemMusicHolder, position: Int) {
        return holder.bindItem(musics[position])
    }

    override fun getItemCount(): Int = musics.size

//    fun addAllData(datas: List<MusicModelResult>) {
//        musics.apply {
//            clear()
//            addAll(datas)
//        }
//
//        notifyDataSetChanged()
//    }



}