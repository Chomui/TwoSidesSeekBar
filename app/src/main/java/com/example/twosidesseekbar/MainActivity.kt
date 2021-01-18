package com.example.twosidesseekbar

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.TypedValue
import android.widget.SeekBar
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<TwoSideSeekBar>(R.id.seekBar).setOnChangeListener(object : TwoSideSeekBar.OnChangeListener {
            override fun onProgressChanged(progress: Int, fromUser: Boolean) {
                findViewById<TextView>(R.id.text).text = progress.toString()
            }
        })
        findViewById<TextView>(R.id.text).setOnClickListener {
            findViewById<TwoSideSeekBar>(R.id.seekBar).setProgressLineHeight(4F.toDp())
            findViewById<TwoSideSeekBar>(R.id.seekBar).setMax(10)
            findViewById<TwoSideSeekBar>(R.id.seekBar).setProgress(-5)
        }
    }

    private fun Float.toDp(): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            this,
            resources.displayMetrics
        )
    }
}


