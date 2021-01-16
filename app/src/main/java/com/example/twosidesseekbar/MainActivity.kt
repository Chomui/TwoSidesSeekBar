package com.example.twosidesseekbar

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.SeekBar
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<TwoSideSeekBar>(R.id.seekBar).setOnChangeListener(object : TwoSideSeekBar.OnChangeListener {
            override fun onProgressChanged(progress: Int) {
                findViewById<TextView>(R.id.text).text = progress.toString()
            }
        })
    }
}