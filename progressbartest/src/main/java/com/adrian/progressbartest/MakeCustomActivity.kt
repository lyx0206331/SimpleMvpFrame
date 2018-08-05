package com.adrian.progressbartest

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.view.animation.*
import android.widget.*
import com.adrian.progressbarlib.MaterialProgressBar
import com.adrian.simplemvpframe.progressbar.FastOutSlowInInterpolator
import java.util.*

class MakeCustomActivity : AppCompatActivity() {

    private var mProgressBar: MaterialProgressBar? = null
    private var mCheckBoxMirror: CheckBox? = null
    private var mCheckBoxReversed: CheckBox? = null
    private var mCheckBoxGradients: CheckBox? = null
    private var mSpinnerInterpolators: Spinner? = null
    private var mSeekBarSectionsCount: SeekBar? = null
    private var mSeekBarStrokeWidth: SeekBar? = null
    private var mSeekBarSeparatorLength: SeekBar? = null
    private var mSeekBarSpeed: SeekBar? = null
    private var mSeekBarFactor: SeekBar? = null
    private var mTextViewFactor: TextView? = null
    private var mTextViewSpeed: TextView? = null
    private var mTextViewStrokeWidth: TextView? = null
    private var mTextViewSeparatorLength: TextView? = null
    private var mTextViewSectionsCount: TextView? = null

    private var mCurrentInterpolator: Interpolator? = null
    private var mStrokeWidth = 4
    private var mSeparatorLength: Int = 0
    private var mSectionsCount: Int = 0
    private var mFactor = 1f
    private var mSpeed = 1f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_make_custom)

        mProgressBar = findViewById(R.id.progressbar)
        mCheckBoxMirror = findViewById(R.id.checkbox_mirror)
        mCheckBoxReversed = findViewById(R.id.checkbox_reversed)
        mCheckBoxGradients = findViewById(R.id.checkbox_gradients)
        mSpinnerInterpolators = findViewById(R.id.spinner_interpolator)
        mSeekBarSectionsCount = findViewById(R.id.seekbar_sections_count)
        mSeekBarStrokeWidth = findViewById(R.id.seekbar_stroke_width)
        mSeekBarSeparatorLength = findViewById(R.id.seekbar_separator_length)
        mSeekBarSpeed = findViewById(R.id.seekbar_speed)
        mSeekBarFactor = findViewById(R.id.seekbar_factor)
        mTextViewSpeed = findViewById(R.id.textview_speed)
        mTextViewSectionsCount = findViewById(R.id.textview_sections_count)
        mTextViewSeparatorLength = findViewById(R.id.textview_separator_length)
        mTextViewStrokeWidth = findViewById(R.id.textview_stroke_width)
        mTextViewFactor = findViewById(R.id.textview_factor)

        findViewById<View>(R.id.button_start).setOnClickListener {
            mProgressBar?.progressiveStart()
        }

        findViewById<View>(R.id.button_stop).setOnClickListener {
            mProgressBar?.progressiveStop()
        }

        mSeekBarFactor?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                mFactor = (progress + 1) / 10f
                mTextViewFactor?.text = "Factor: $mFactor"
                setInterpolator(mSpinnerInterpolators!!.selectedItemPosition)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {

            }
        })

        mSeekBarSpeed?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                mSpeed = (progress.toFloat() + 1) / 10
                mTextViewSpeed?.text = "Speed: $mSpeed"
                mProgressBar?.setMaterialProgressDrawableSpeed(mSpeed)
                mProgressBar?.setMaterialProgressDrawableProgressiveStartSpeed(mSpeed)
                mProgressBar?.setMaterialProgressDrawableProgressiveStopSpeed(mSpeed)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {

            }
        })

        mSeekBarSectionsCount?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                mSectionsCount = progress + 1
                mTextViewSectionsCount?.text = "Sections count: $mSectionsCount"
                mProgressBar?.setMaterialProgressDrawableSectionsCount(mSectionsCount)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {

            }
        })

        mSeekBarSeparatorLength?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                mSeparatorLength = progress
                mTextViewSeparatorLength?.text = String.format(Locale.US, "Separator length: %ddp", mSeparatorLength)
                mProgressBar?.setMaterialProgressDrawableSeparatorLength(dpToPx(mSeparatorLength))
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {

            }
        })

        mSeekBarStrokeWidth?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                mStrokeWidth = progress
                mTextViewStrokeWidth?.text = String.format(Locale.US, "Stroke width: %ddp", mStrokeWidth)
                mProgressBar?.setMaterialProgressDrawableStrokeWidth(dpToPx(mStrokeWidth).toFloat())
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {

            }
        })

        mCheckBoxGradients?.setOnCheckedChangeListener { _, isChecked -> mProgressBar?.setMaterialProgressDrawableUseGradients(isChecked) }

        mCheckBoxMirror?.setOnCheckedChangeListener { _, isChecked -> mProgressBar?.setMaterialProgressDrawableMirrorMode(isChecked) }

        mCheckBoxReversed?.setOnCheckedChangeListener { _, isChecked -> mProgressBar?.setMaterialProgressDrawableReversed(isChecked) }

        mSeekBarSeparatorLength?.progress = 4
        mSeekBarSectionsCount?.progress = 4
        mSeekBarStrokeWidth?.progress = 4
        mSeekBarSpeed?.progress = 9
        mSeekBarFactor?.progress = 9

        mSpinnerInterpolators?.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, resources.getStringArray(R.array.interpolators))
        mSpinnerInterpolators?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                setInterpolator(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }
        mSpinnerInterpolators?.setSelection(4)
    }

    private fun setInterpolator(position: Int) {
        when (position) {
            1 -> {
                mCurrentInterpolator = LinearInterpolator()
                mSeekBarFactor?.isEnabled = false
            }
            2 -> {
                mCurrentInterpolator = AccelerateDecelerateInterpolator()
                mSeekBarFactor?.isEnabled = false
            }
            3 -> {
                mCurrentInterpolator = DecelerateInterpolator(mFactor)
                mSeekBarFactor?.isEnabled = true
            }
            4 -> {
                mCurrentInterpolator = FastOutSlowInInterpolator()
                mSeekBarFactor?.isEnabled = true
            }
            0 -> {
                mCurrentInterpolator = AccelerateInterpolator(mFactor)
                mSeekBarFactor?.isEnabled = true
            }
            else -> {
                mCurrentInterpolator = AccelerateInterpolator(mFactor)
                mSeekBarFactor?.isEnabled = true
            }
        }

        mProgressBar?.setMaterialProgressDrawableInterpolator(mCurrentInterpolator)
        mProgressBar?.setMaterialProgressDrawableColors(resources.getIntArray(R.array.gplus_colors))
    }

    fun dpToPx(dp: Int): Int {
        val r = resources
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dp.toFloat(), r.displayMetrics).toInt()
    }

}
