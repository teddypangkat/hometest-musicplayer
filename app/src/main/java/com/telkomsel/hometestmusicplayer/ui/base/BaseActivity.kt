package com.telkomsel.hometestmusicplayer.ui.base

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.viewbinding.ViewBinding
import com.telkomsel.hometestmusicplayer.R

abstract class BaseActivity<VB : ViewBinding> : AppCompatActivity() {

    private var mToolbar: Toolbar? = null

    lateinit var viewBinding: VB
    var dialog: AlertDialog? = null

    private fun initUiBinding() {
        if (!::viewBinding.isInitialized) {
            viewBinding = getUiBinding()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initUiBinding()
        setContentView(viewBinding.root)
        setupToolbar()
        onFirstLaunch(savedInstanceState)
        initUiListener()
    }


    abstract fun onFirstLaunch(savedInstanceState: Bundle?)
    abstract fun initUiListener()
    abstract fun bindToolbar(): Toolbar?
    abstract fun getUiBinding(): VB

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        android.R.id.home -> {
            onBackPressed()
            true
        }
        else -> false
    }


    private fun setupDialog() {
        val builder = AlertDialog.Builder(this@BaseActivity)
        builder.setCancelable(false)

        // if you want user to wait for some process to finish,
        builder.setView(R.layout.layout_loading_dialog)

        dialog = builder.create()
        dialog!!.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
    }

    fun showDialog() {
        dialog?.show()
    }

    fun dismissDialog() {
        dialog?.dismiss()
    }

    private fun setupToolbar() {
        setupDialog()
        bindToolbar()?.let {
            mToolbar = it
            setSupportActionBar(mToolbar)
            supportActionBar?.apply {
                setDisplayShowTitleEnabled(true)
                setDisplayHomeAsUpEnabled(true)
            }
        }
    }

    fun getToolbar(): Toolbar? = mToolbar
}