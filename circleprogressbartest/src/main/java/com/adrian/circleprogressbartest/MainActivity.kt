package com.adrian.circleprogressbartest

import android.animation.ValueAnimator
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.adrian.circleprogressbarlib.CircleProgressBar
import com.adrian.circleprogressbarlib.FantasticButton
import com.adrian.circleprogressbarlib.Utils
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var mLineProgressBar: CircleProgressBar? = null
    private var mSolidProgressBar: CircleProgressBar? = null
    private var mCustomProgressBar1: CircleProgressBar? = null
    private var mCustomProgressBar2: CircleProgressBar? = null
    private var mCustomProgressBar3: CircleProgressBar? = null
    private var mCustomProgressBar4: CircleProgressBar? = null
    private var mCustomProgressBar5: CircleProgressBar? = null
    private var mCustomProgressBar6: CircleProgressBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mLineProgressBar = findViewById(R.id.line_progress)
        mSolidProgressBar = findViewById(R.id.solid_progress)
        mCustomProgressBar1 = findViewById(R.id.custom_progress1)
        mCustomProgressBar2 = findViewById(R.id.custom_progress2)
        mCustomProgressBar3 = findViewById(R.id.custom_progress3)
        mCustomProgressBar4 = findViewById(R.id.custom_progress4)
        mCustomProgressBar5 = findViewById(R.id.custom_progress5)
        mCustomProgressBar6 = findViewById(R.id.custom_progress6)

        mCustomProgressBar5?.mProgressFormatter = object : CircleProgressBar.ProgressFormatter {
            override fun format(progress: Int, max: Int): CharSequence {
                return "${progress}s"
            }

        }
    }

    override fun onResume() {
        super.onResume()
        simulateProgress()
    }

    private fun simulateProgress() {
        val animator: ValueAnimator = ValueAnimator.ofInt(0, 100)
        animator.addUpdateListener {
            val progress: Int = it.animatedValue as Int
            mLineProgressBar?.mProgress = progress
            mSolidProgressBar?.mProgress = progress
            mCustomProgressBar1?.mProgress = progress
//            mCustomProgressBar2?.mProgress = progress
            mCustomProgressBar3?.mProgress = progress
            mCustomProgressBar4?.mProgress = progress
//            mCustomProgressBar5?.mProgress = progress
            mCustomProgressBar6?.mProgress = progress
        }
        animator.repeatCount = ValueAnimator.INFINITE
        animator.duration = 4000
        animator.start()

//        mSolidProgressBar?.startAnimator(2000, 0, 60)
        mCustomProgressBar2?.startAnimator(3000, 100, 30)
//        mCustomProgressBar5?.startAnimator(4000)
        mCustomProgressBar5?.mOnPressedListener = object : CircleProgressBar.OnPressedListener {
            override fun onPressStart() {
                toast("press start")
            }

            override fun onPressProcess(progress: Int) {
                Utils.logE("PROGRESS", "progress: $progress")
            }

            override fun onPressStop(progress: Int) {
                toast("press stop: $progress")
            }

        }

        fantastic_btn.startAnimator(4000)
    }

    private fun toast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}
