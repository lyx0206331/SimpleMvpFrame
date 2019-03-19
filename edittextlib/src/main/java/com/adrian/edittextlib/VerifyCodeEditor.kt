package com.adrian.edittextlib

import android.content.Context
import android.support.annotation.ColorInt
import android.support.annotation.DrawableRes
import android.support.v4.content.ContextCompat
import android.support.v7.widget.AppCompatEditText
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.view.inputmethod.InputConnectionWrapper
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.px2sp

/**
 * date:2019/3/13 19:57
 * author:RanQing
 * description:
 */
open class VerifyCodeEditor @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : LinearLayout(context, attrs, defStyleAttr) {
    companion object {
        const val DEFAULT_INPUT_COUNT = 4
        const val DEFAULT_LINE_HEIGHT = 1
        const val DEFAULT_INPUT_SPACE = 15
        const val DEFAULT_LINE_SPACE = 8
        const val DEFAULT_TEXT_SIZE = 20f
    }

    private val editTextList by lazy { arrayListOf<EditorHelper>() }
    private val underlineList by lazy { arrayListOf<View>() }

    var currentPosition = 0
        set(value) {
            field = if (value > editTextList.size - 1) editTextList.size - 1 else value
            editTextList[field].requestFocus()
            editTextList[field].setSelection(editTextList[field].text?.length ?: 0)
        }
    var listener: InputCompleteListener? = null
    //有焦点时下划线颜色
    @ColorInt
    var lineFocusColor = ContextCompat.getColor(context, android.R.color.holo_blue_bright)
        set(value) {
            field = value
            invalidate()
        }
    //无焦点时下划线颜色
    @ColorInt
    var lineDefaultColor = ContextCompat.getColor(context, android.R.color.holo_blue_light)
        set(value) {
            field = value
            invalidate()
        }

    //是否让所有的线都高亮
    var isAllLineLight = false
        set(value) {
            field = value
            if (field) {
                underlineList.forEach {
                    it.backgroundColor = lineFocusColor
                }
            }
        }
    //输入框数量
    var inputCount = DEFAULT_INPUT_COUNT
        set(value) {
            field = value
            invalidate()
        }
    //下划线高度
    var underlineHeight = dp2px(DEFAULT_LINE_HEIGHT)
        set(value) {
            field = value
            invalidate()
        }
    //输入框间距
    var inputSpace = dp2px(DEFAULT_INPUT_SPACE)
        set(value) {
            field = value
            invalidate()
        }
    //文字到下划线的间距
    var lineSpace = dp2px(DEFAULT_LINE_SPACE)
        set(value) {
            field = value
            invalidate()
        }
    //输入文字大小
    var textSize = dp2px(DEFAULT_TEXT_SIZE.toInt()).toFloat()
        set(value) {
            field = value
            invalidate()
        }
    //输入文字颜色
    var textColor = ContextCompat.getColor(context, android.R.color.holo_blue_dark)
        set(value) {
            field = value
            invalidate()
        }
    //光标
    @DrawableRes
    var cursorDrawable = R.drawable.shape_edit_cursor
        set(value) {
            field = value
            invalidate()
        }

    //输入内容
    var content = ""
        set(value) {
            field = value
            if (value.length == inputCount) {
                for ((i, editor) in editTextList.withIndex()) {
                    editor.setText("${value[i]}")
                }
            }
        }
        get() {
            if (editTextList.isEmpty()) return ""
            val builder = StringBuilder()
            editTextList.forEach {
                builder.append(it.text.toString())
            }
            return builder.toString()
        }
    //是否输入完成
    var isInputComplete = false
        get() {
            if (editTextList.isEmpty()) return false
            editTextList.forEach {
                if (it.text.isNullOrEmpty()) return false
            }
            return true
        }

    init {
        logE("init0")
        val typeArray = context.obtainStyledAttributes(attrs, R.styleable.VerifyCodeEditor, defStyleAttr, 0)
        typeArray?.let {
            inputCount = it.getInteger(R.styleable.VerifyCodeEditor_vceInputCount, DEFAULT_INPUT_COUNT)
            underlineHeight = it.getDimensionPixelSize(R.styleable.VerifyCodeEditor_vceUnderlineHeight, DEFAULT_LINE_HEIGHT)
            inputSpace = it.getDimensionPixelSize(R.styleable.VerifyCodeEditor_vceInputSpace, DEFAULT_INPUT_SPACE)
            lineSpace = it.getDimensionPixelSize(R.styleable.VerifyCodeEditor_vceUnderlineSpace, DEFAULT_LINE_SPACE)
            textSize = it.getDimension(R.styleable.VerifyCodeEditor_vceInputTextSize, DEFAULT_TEXT_SIZE)
            textColor = it.getColor(R.styleable.VerifyCodeEditor_vceInputTextColor, ContextCompat.getColor(context, android.R.color.holo_blue_dark))
            lineFocusColor = it.getColor(R.styleable.VerifyCodeEditor_vceFocusColor, ContextCompat.getColor(context, android.R.color.holo_blue_bright))
            lineDefaultColor = it.getColor(R.styleable.VerifyCodeEditor_vceUnfocusColor, ContextCompat.getColor(context, android.R.color.holo_blue_light))
            cursorDrawable = it.getResourceId(R.styleable.VerifyCodeEditor_vceCursorDrawable, R.drawable.shape_edit_cursor)
        }
        typeArray.recycle()
        initView()
    }

    private fun initView() {
        logE("init1")
        if (inputCount <= 0) {
            return
        }
        logE("init2")
        orientation = HORIZONTAL
        gravity = Gravity.CENTER

        for (i in 0 until inputCount) {
            val flLp = LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f)
            flLp.setMargins(if (i == 0) 0 else inputSpace, 0, 0, 0)
            val frameLayout = FrameLayout(context)
            frameLayout.layoutParams = flLp

            val etLp = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT)
            val editor = EditorHelper(context)
            editor.background = null
            editor.setPadding(0, 0, 0, lineSpace)
            editor.maxLines = 1
            editor.textSize = px2sp(textSize.toInt())
            editor.setTextColor(textColor)
            val filters = arrayOf(InputFilter.LengthFilter(1))
            editor.filters = filters
            editor.inputType = InputType.TYPE_CLASS_NUMBER
            editor.gravity = Gravity.CENTER

            //修改光标的颜色
            try {
                val f = TextView::class.java.getDeclaredField("mCursorDrawableRes")
                f.isAccessible = true
                f.set(editor, cursorDrawable)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            editor.layoutParams = etLp
            frameLayout.addView(editor)

            val lineLp = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, underlineHeight)
            lineLp.gravity = Gravity.BOTTOM
            val underline = View(context)
            underline.backgroundColor = lineDefaultColor
            underline.layoutParams = lineLp
            frameLayout.addView(underline)

            addView(frameLayout)
            editTextList.add(editor)
            underlineList.add(underline)
        }

        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (!s.isNullOrEmpty() && currentPosition < editTextList.size - 1) {
                    currentPosition++
                    editTextList[currentPosition].requestFocus()
                }
                if (isInputComplete) {
                    listener?.inputComplete(this@VerifyCodeEditor, content)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        val focusChangeListener = OnFocusChangeListener { v, hasFocus ->
            for ((index, value) in editTextList.withIndex()) {
                if (value.isFocused) {
                    currentPosition = index
                }
                if (!isAllLineLight) {
                    underlineList[index].backgroundColor = lineDefaultColor
                }
            }
            if (!isAllLineLight) {
                underlineList[currentPosition].backgroundColor = lineFocusColor
            }
        }

        val keyListener = object : OnKeyListener {
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
                //监听Delelte键
                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    if (event?.action != KeyEvent.ACTION_DOWN) {
                        return true
                    }
                    if (editTextList[currentPosition].text.isNullOrEmpty()) {
                        if (currentPosition <= 0) {
                            return true
                        }
                        //跳到前一个不为空的Editor
                        for (i in currentPosition downTo 0) {
                            currentPosition = i
                            if (!editTextList[i].text.isNullOrEmpty()) {
                                break
                            }
                        }
                    }
                    editTextList[currentPosition].requestFocus()
                    editTextList[currentPosition].text?.clear()
                    return true
                }
                return false
            }
        }

        editTextList.forEach {
            it.addTextChangedListener(textWatcher)
            it.onFocusChangeListener = focusChangeListener
            it.keyListener = keyListener
        }

        editTextList[0].requestFocus()
    }

    /**
     * 转换字体大小
     */
    private inline fun parseTextSize(size: Float): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, size, resources.displayMetrics)
    }

    fun dp2px(dp: Int): Int {
        return (dp * context.resources.displayMetrics.density + .5f).toInt()
    }

    private fun logE(log: String) {
        Log.e("VerifyCodeEditor", "log:$log")
    }

    interface InputCompleteListener {
        fun inputComplete(editor: VerifyCodeEditor, content: String)
    }
}

class EditorHelper(context: Context) : AppCompatEditText(context) {

    var keyListener: OnKeyListener? = null

    override fun onCreateInputConnection(outAttrs: EditorInfo?): InputConnection {
        return MyInputConnection(super.onCreateInputConnection(outAttrs), true)
    }

    private inner class MyInputConnection(target: InputConnection, mutable: Boolean) : InputConnectionWrapper(target, mutable) {

        /**
         * 覆盖事件传递
         */
        override fun sendKeyEvent(event: KeyEvent?): Boolean {
            event?.let {
                keyListener?.onKey(this@EditorHelper, it.keyCode, it)
            }
            return super.sendKeyEvent(event)
        }

        override fun deleteSurroundingText(beforeLength: Int, afterLength: Int): Boolean {
            //在删除时，输入框无内容，或者删除后输入框无内容
            if (beforeLength == 1 || afterLength == 0 || beforeLength == 0) {
                return sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL)) && sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL))
            }
            return super.deleteSurroundingText(beforeLength, afterLength)
        }
    }
}