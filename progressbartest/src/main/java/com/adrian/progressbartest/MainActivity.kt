package com.adrian.progressbartest

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.widget.ProgressBar
import com.adrian.progressbarlib.SmoothProgressBar
import com.adrian.progressbarlib.SmoothProgressBarUtils
import com.adrian.progressbarlib.SmoothProgressDrawable

class MainActivity : Activity() {

    private var mProgressBar1: ProgressBar? = null
    private var mGoogleNow: SmoothProgressBar? = null
    private var mPocketBar: SmoothProgressBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mProgressBar1 = findViewById(R.id.progressbar2)
        mPocketBar = findViewById(R.id.pocket)

        mProgressBar1!!.indeterminateDrawable = SmoothProgressDrawable.Builder(this).interpolator(AccelerateInterpolator()).build()

        mGoogleNow = findViewById(R.id.google_now)
        mPocketBar!!.setSmoothProgressDrawableBackgroundDrawable(
                SmoothProgressBarUtils.generateDrawableWithColors(
                        resources.getIntArray(R.array.pocket_background_colors),
                        (mPocketBar!!.indeterminateDrawable as SmoothProgressDrawable).mStrokeWidth))

        findViewById<View>(R.id.button_make).setOnClickListener {
            val intent = Intent(this@MainActivity, MakeCustomActivity::class.java)
            startActivity(intent)
        }

        findViewById<View>(R.id.start).setOnClickListener { mPocketBar!!.progressiveStart() }

        findViewById<View>(R.id.finish).setOnClickListener { mPocketBar!!.progressiveStop() }
    }
}
