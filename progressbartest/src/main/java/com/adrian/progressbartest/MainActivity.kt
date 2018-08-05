package com.adrian.progressbartest

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.widget.ProgressBar
import com.adrian.progressbarlib.MaterialProgressBar
import com.adrian.progressbarlib.MaterialProgressBarUtils
import com.adrian.progressbarlib.MaterialProgressDrawable

class MainActivity : AppCompatActivity() {

    private var mProgressBar1: ProgressBar? = null
    private var mGoogleNow: MaterialProgressBar? = null
    private var mPocketBar: MaterialProgressBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mProgressBar1 = findViewById(R.id.progressbar2)
        mPocketBar = findViewById(R.id.pocket)

        mProgressBar1?.indeterminateDrawable = MaterialProgressDrawable.Builder(this).interpolator(AccelerateInterpolator()).build()

        mGoogleNow = findViewById(R.id.google_now)
        mPocketBar?.setMaterialProgressDrawableBackgroundDrawable(
                MaterialProgressBarUtils.generateDrawableWithColors(
                        resources.getIntArray(R.array.pocket_background_colors),
                        (mPocketBar!!.indeterminateDrawable as MaterialProgressDrawable).mStrokeWidth))

        findViewById<View>(R.id.button_make).setOnClickListener {
            val intent = Intent(this@MainActivity, MakeCustomActivity::class.java)
            startActivity(intent)
        }

        findViewById<View>(R.id.start).setOnClickListener { mPocketBar!!.progressiveStart() }

        findViewById<View>(R.id.finish).setOnClickListener { mPocketBar!!.progressiveStop() }
    }
}
