package com.telkomsel.hometestmusicplayer.ui.main.view

import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.TranslateAnimation
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.telkomsel.hometestmusicplayer.R
import com.telkomsel.hometestmusicplayer.databinding.ActivityMainBinding
import com.telkomsel.hometestmusicplayer.ui.base.BaseActivity
import com.telkomsel.hometestmusicplayer.ui.main.adapter.MusicAdapter
import com.telkomsel.hometestmusicplayer.ui.main.viewmodel.MusicViewModel


class MainActivity : BaseActivity<ActivityMainBinding>() {

    private lateinit var musicViewModel: MusicViewModel
    private lateinit var musicAdapter: MusicAdapter
    private var currentPosition = -1
    private var lastPosition = -1
    private var mediaPlayer: MediaPlayer? = null
    private var musicUrl = ""

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

    //this function for setting visible enable indicator music
    private fun settingIndicatorMusic(position: Int, isPlay: Boolean) {
        val music = musicAdapter.musics[position]
        music.isPlay = isPlay
        musicAdapter.notifyItemChanged(position)
    }

    private fun initAdapter() {
        if (!::musicAdapter.isInitialized) {
            musicAdapter = MusicAdapter {
                //Log.d("POSITION" , it.toString())
                currentPosition = it
                musicUrl = musicAdapter.musics[it].previewUrl
                settingIndicatorMusic(currentPosition, true)
                stopPlayer()
                playMusic(musicUrl)


                //setting for visible enable music indicator
                if (lastPosition != -1) {
                         settingIndicatorMusic(lastPosition, false)
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


    //play music from api
    fun playMusic(url: String) {
        mediaPlayer = MediaPlayer().apply {
            setAudioStreamType(AudioManager.STREAM_MUSIC)
            setDataSource(url)
            prepare() // might take long! (for buffering, etc)
            start()
        }
        settingShowMusicController(View.VISIBLE)
    }


    fun stopPlayer() {
        mediaPlayer = if (mediaPlayer != null && mediaPlayer!!.isPlaying()) {
            mediaPlayer!!.stop()
            null
        } else {
            null
        }
    }

    fun pausePlayer() {
        if (mediaPlayer!!.isPlaying) {
            mediaPlayer!!.pause()
        }
    }

    //show music controller
    fun settingShowMusicController(view: Int) {
        viewBinding.layoutMusicController.visibility = view
    }

    override fun onStop() {
        super.onStop()
        stopPlayer()
    }

    override fun onPause() {
        super.onPause()
        stopPlayer()
    }

    override fun initUiListener() {

        viewBinding.edSearch.setOnEditorActionListener { _, actionId, event ->
            if (actionId === EditorInfo.IME_ACTION_SEARCH) {
                val valueSearch = viewBinding.edSearch.text.toString()

                if (valueSearch.isEmpty())
                    viewBinding.edSearch.error = "Not be empty"
                 else
                    searchMusic(valueSearch)

                return@setOnEditorActionListener true
            }
            false
        }


        viewBinding.btnPause.setOnClickListener {

            if (mediaPlayer!!.isPlaying) {
                viewBinding.btnPause.setImageResource(R.drawable.ic_play)
                mediaPlayer!!.pause()
            } else {
                viewBinding.btnPause.setImageResource(R.drawable.ic_pause)
                playMusic(musicUrl)
            }
        }


        //next play music
        viewBinding.btnNext.setOnClickListener {
            val music = musicAdapter.musics
            val nextPosition = currentPosition + 1
            //cek validate limit list
            if (currentPosition < music.size) {
                //update view current
                stopPlayer()
                settingIndicatorMusic(currentPosition, false)

                settingIndicatorMusic(nextPosition, true)
                playMusic(music[nextPosition].previewUrl)
                //update current position
                currentPosition = nextPosition

            }
        }


        //next play music
        viewBinding.btnPrev.setOnClickListener {
            val music = musicAdapter.musics
            val prevPosition = currentPosition  - 1
            //cek validate limit list
            if (currentPosition != 0) {
                //update view current
                stopPlayer()
                settingIndicatorMusic(currentPosition, false)

                settingIndicatorMusic(prevPosition, true)
                playMusic(music[prevPosition].previewUrl)
                //update current position
                currentPosition = prevPosition

            }
        }

    }

    override fun bindToolbar(): Toolbar? = null

    override fun getUiBinding() = ActivityMainBinding.inflate(layoutInflater)


}