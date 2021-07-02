package com.telkomsel.hometestmusicplayer.ui.main.view

import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.telkomsel.hometestmusicplayer.databinding.ActivityMainBinding
import com.telkomsel.hometestmusicplayer.ui.base.BaseActivity
import com.telkomsel.hometestmusicplayer.ui.main.adapter.MusicAdapter
import com.telkomsel.hometestmusicplayer.ui.main.viewmodel.MusicViewModel


class MainActivity : BaseActivity<ActivityMainBinding>(){

    private lateinit var musicViewModel : MusicViewModel
    private lateinit var musicAdapter: MusicAdapter
    private var currentPosition = -1
    private var lastPosition = -1
    private var mediaPlayer: MediaPlayer? = null

    override fun onFirstLaunch(savedInstanceState: Bundle?) {
        musicViewModel = ViewModelProvider(this).get(MusicViewModel::class.java)
        initAdapter()
        initRecyclerView()
    }

    private fun initRecyclerView() {
        viewBinding.recMusic?.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = musicAdapter
            addItemDecoration(
                DividerItemDecoration(
                    this@MainActivity,
                    LinearLayoutManager.HORIZONTAL
                )
            )
        }
    }

    private fun initAdapter() {
        if (!::musicAdapter.isInitialized) {
            musicAdapter = MusicAdapter {
                currentPosition = it
                    val music = musicAdapter.musics[currentPosition]
                    music.isPlay = true
                    musicAdapter.notifyItemChanged(currentPosition)
                    stopPlayer()
                    playMusic(music.previewUrl)

                if (lastPosition != -1) {
                    val lastMusic = musicAdapter.musics[lastPosition]
                    lastMusic.isPlay = false
                    musicAdapter.notifyItemChanged(lastPosition)
                    //stopMusic()
                }
                    lastPosition = currentPosition
                //playMusic(music.previewUrl)

            }
        }
    }

    //call search music from api
    fun searchMusic(valueSearch: String) {
        showDialog()
        observeData()
        musicViewModel.searchMusic(valueSearch, "music")
    }

    fun observeData() {
        musicViewModel.musicsLiveData.observe(this, Observer {

            //if call search from api success
            musicAdapter.musics = it.results
            dismissDialog()
        })

        musicViewModel.error.observe(this, Observer {

            //if error
            dismissDialog()
            Toast.makeText(this@MainActivity, it.message, Toast.LENGTH_LONG).show()
        })
    }


    fun playMusic(url: String) {
        mediaPlayer = MediaPlayer().apply {
            setAudioStreamType(AudioManager.STREAM_MUSIC)
            setDataSource(url)
            prepare() // might take long! (for buffering, etc)
            start()
        }

    }

    fun stopPlayer() {
        mediaPlayer = if (mediaPlayer != null && mediaPlayer!!.isPlaying()) {
            mediaPlayer!!.stop()
            null
        } else {
            null
        }
    }

    override fun initUiListener() {

        viewBinding.edSearch.setOnEditorActionListener { _, actionId, event ->
            if (actionId === EditorInfo.IME_ACTION_SEARCH) {
                searchMusic(viewBinding.edSearch.text.toString())
                return@setOnEditorActionListener true
            }
            false
        }

    }

    override fun bindToolbar(): Toolbar?  = null

    override fun getUiBinding() = ActivityMainBinding.inflate(layoutInflater)




}