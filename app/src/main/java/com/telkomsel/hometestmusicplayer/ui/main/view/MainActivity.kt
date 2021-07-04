package com.telkomsel.hometestmusicplayer.ui.main.view

import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.telkomsel.hometestmusicplayer.R
import com.telkomsel.hometestmusicplayer.databinding.ActivityMainBinding
import com.telkomsel.hometestmusicplayer.ui.base.BaseActivity
import com.telkomsel.hometestmusicplayer.ui.main.adapter.MusicAdapter
import com.telkomsel.hometestmusicplayer.ui.main.viewmodel.MusicViewModel
import com.telkomsel.hometestmusicplayer.util.PositionNumberList


class MainActivity : BaseActivity<ActivityMainBinding>() {

    private lateinit var musicViewModel: MusicViewModel
    private lateinit var musicAdapter: MusicAdapter
    private var currentPosition = -1
    private var lastPosition = -1
    private var mediaPlayer: MediaPlayer? = null
    private var musicUrl = ""
    private var duration = 0

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
                duration = musicAdapter.musics[it].trackTimeMillis
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

    //observer data from API
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
        viewBinding.btnPause.setImageResource(R.drawable.ic_pause)
        settingShowMusicController(View.VISIBLE)
        updateSeekBar()
    }

    private fun updateSeekBar() {
        viewBinding.seekBarMusic.max = mediaPlayer!!.duration/1000
        //update status seekbar for 1 second by mediaplayer current status
        val mHandler = Handler()
        runOnUiThread(object : Runnable {
            override fun run() {
                if (mediaPlayer != null) {
                    val mCurrentPosition: Int = mediaPlayer!!.getCurrentPosition() / 1000
                    viewBinding.seekBarMusic.setProgress(mCurrentPosition)
                }
                mHandler.postDelayed(this, 1000)
            }
        })



        viewBinding.seekBarMusic.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if(mediaPlayer != null && fromUser){
                    mediaPlayer!!.seekTo(progress * 1000);
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
    }



    fun stopPlayer() {
        mediaPlayer = if (mediaPlayer != null && mediaPlayer!!.isPlaying()) {
            mediaPlayer!!.stop()
            null
        } else {
            null
        }
    }

    //setting for show/hide layout music controller
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
                    currentPosition = -1
                    lastPosition = -1
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
            val nextPosition = PositionNumberList.nextPosition(currentPosition)
            //cek validate limit list
            if (currentPosition < music.size) {
                //update view current
                stopPlayer()
                settingIndicatorMusic(currentPosition, false)

                settingIndicatorMusic(nextPosition, true)
                playMusic(music[nextPosition].previewUrl)
                //update current position
                currentPosition = nextPosition
                musicUrl = musicAdapter.musics[nextPosition].previewUrl

            }
        }


        //next play music
        viewBinding.btnPrev.setOnClickListener {
            val music = musicAdapter.musics
            val prevPosition = PositionNumberList.prevPosition(currentPosition)
            //cek validate limit list
            if (currentPosition != 0) {
                //update view current
                stopPlayer()
                settingIndicatorMusic(currentPosition, false)

                settingIndicatorMusic(prevPosition, true)
                playMusic(music[prevPosition].previewUrl)
                //update current position
                currentPosition = prevPosition
                musicUrl = musicAdapter.musics[prevPosition].previewUrl


            }
        }

    }

    override fun bindToolbar(): Toolbar? = null

    override fun getUiBinding() = ActivityMainBinding.inflate(layoutInflater)


}