package com.adrian.transitionanimlib

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.RelativeLayout
import com.adrian.transitionanimlib.bean.InfoBean
import com.adrian.transitionanimlib.expose.CirleExposeView
import com.adrian.transitionanimlib.expose.base.ExposeView
import com.adrian.transitionanimlib.listener.ExposeListener
import com.adrian.transitionanimlib.listener.TransitionListener
import com.adrian.transitionanimlib.method.InflateShowMethod
import com.adrian.transitionanimlib.method.NoneShowMethod
import com.adrian.transitionanimlib.method.ShowMethod
import com.adrian.transitionanimlib.util.BitmapUtils
import com.adrian.transitionanimlib.util.BitmapUtils.createBitmap
import java.lang.ref.WeakReference
import java.util.HashMap

/**
 * date:2018/8/9 16:38
 * author：RanQing
 * description：
 */
class TransitionsHeleper {

    private var activity: Activity? = null

    private var showMethod: ShowMethod? = null

    private var transitionListener: TransitionListener? = null

    private var transitionDuration: Long = 0

    private var exposeView: ExposeView? = null

    private var exposeColor: Int = 0

    private var exposeAcceleration: Int = 0

    private var useInflateExpose: Boolean = false

    private var targetView: View? = null

    constructor(builder: TransitionBuilder) {
        activity = builder.activity
        showMethod = builder.showMethod
        exposeView = builder.exposeView
        exposeColor = builder.exposeColor
        exposeAcceleration = builder.exposeAcceleration
        useInflateExpose = builder.useInflateExpose
        transitionListener = builder.transitionListener
        transitionDuration = builder.transitionDuration
        targetView = builder.targetView
        sTransitionMap[activity?.javaClass!!.name] = WeakReference(this)
    }

    companion object {

        private val sInfoMap = HashMap<String, InfoBean<Any>>()
        private val sTransitionMap = HashMap<String, WeakReference<TransitionsHeleper>>()

        @JvmStatic
        fun build(activity: Activity?): TransitionBuilder {
            if (activity == null) {
                throw IllegalArgumentException("You cannot start a load on a null Activity")
            }
            return TransitionBuilder(activity)
        }

        @JvmStatic
        private fun create(builder: TransitionBuilder): TransitionsHeleper {
            return TransitionsHeleper(builder)
        }

        @JvmStatic
        fun startActivity(activity: Activity, intent: Intent, view: View) {
            startActivity(activity, intent, view, null)
        }

        @JvmStatic
        fun startActivity(activity: Activity, intent: Intent, view: View, load: Any?) {
            startEngine(activity, null, intent, view, load, false, -1, null, false, null)
        }


        @JvmStatic
        fun startActivity(activity: Activity, cls: Class<*>, view: View) {
            startActivity(activity, cls, view, null)
        }

        @JvmStatic
        fun startActivity(activity: Activity, cls: Class<*>, view: View, load: Any?) {
            startEngine(activity, cls, null, view, load, false, -1, null, false, null)
        }

        //Activity ForResult
        @JvmStatic
        fun startActivityForResult(activity: Activity, intent: Intent, requestCode: Int, options: Bundle, view: View) {
            startActivityForResult(activity, intent, requestCode, options, view, null)
        }

        @JvmStatic
        fun startActivityForResult(activity: Activity, intent: Intent, requestCode: Int, options: Bundle, view: View, load: Any?) {
            startEngine(activity, null, intent, view, load, true, requestCode, options, false, null)
        }

        @JvmStatic
        fun startActivityForResult(activity: Activity, cls: Class<*>, requestCode: Int, options: Bundle, view: View) {
            startActivityForResult(activity, cls, requestCode, options, view, null)
        }

        @JvmStatic
        fun startActivityForResult(activity: Activity, cls: Class<*>, requestCode: Int, options: Bundle, view: View, load: Any?) {
            startEngine(activity, cls, null, view, load, true, requestCode, options, false, null)
        }

        //Fragment ForResult
        @JvmStatic
        fun startActivityForResult(fragment: Fragment, intent: Intent, requestCode: Int, options: Bundle, view: View) {
            startActivityForResult(fragment, intent, requestCode, options, view, null)
        }

        @JvmStatic
        fun startActivityForResult(fragment: Fragment, intent: Intent, requestCode: Int, options: Bundle, view: View, load: Any?) {
            startEngine(fragment.activity, null, intent, view, load, true, requestCode, options, true, fragment)
        }

        @JvmStatic
        fun startActivityForResult(fragment: Fragment, cls: Class<*>, requestCode: Int, options: Bundle, view: View) {
            startActivityForResult(fragment, cls, requestCode, options, view, null)
        }

        @JvmStatic
        fun startActivityForResult(fragment: Fragment, cls: Class<*>, requestCode: Int, options: Bundle, view: View, load: Any?) {
            startEngine(fragment.activity, cls, null, view, load, true, requestCode, options, true, fragment)
        }


        private fun startEngine(activity: Activity?, cls: Class<*>?, intent: Intent?, view: View?, load: Any?,
                                isForResult: Boolean, requestCode: Int, options: Bundle?,
                                isFragment: Boolean, fragment: Fragment?) {
            var intent = intent
            if (activity == null) {
                throw IllegalArgumentException("You cannot start with a null activity")
            }
            if (intent == null) {
                intent = Intent(activity, cls)
            }
            if (view == null) {
                throw IllegalArgumentException("You cannot start a load on a null View")
            }
            val finalIntent = intent
            view.post {
                val bean = InfoBean<Any>()
                //get statusBar height
                view.getWindowVisibleDisplayFrame(bean.originRect)
                bean.statusBarHeight = bean.originRect.top
                //get Origin View's rect
                view.getGlobalVisibleRect(bean.originRect)
                bean.originWidth = view.width
                bean.originHeight = view.height
                if (load == null) {
                    bean.bitmap = createBitmap(view, bean.originWidth, bean.originHeight, false)
                } else {
                    if (load is Int || load is String) {
                        bean.setLoad(load)
                    } else {
                        bean.bitmap = createBitmap(view, bean.originWidth, bean.originHeight, false)
                    }
                }
                finalIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                sInfoMap.put(finalIntent.component!!.className, bean)
                if (!isForResult) {
                    activity.startActivity(finalIntent)
                } else {
                    if (!isFragment) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            activity.startActivityForResult(finalIntent, requestCode, options)
                        } else {
                            activity.startActivityForResult(finalIntent, requestCode)
                        }
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            fragment!!.startActivityForResult(finalIntent, requestCode, options)
                        } else {
                            fragment!!.startActivityForResult(finalIntent, requestCode)
                        }
                    }
                }
            }
        }

        @JvmStatic
        fun unbind(activity: Activity) {
            if (sTransitionMap[activity.javaClass.name] != null) {
                val weakT = sTransitionMap[activity.javaClass.name]
                if (weakT?.get() != null) {
                    if (weakT.get()!!.exposeView != null) {
                        weakT.get()!!.exposeView?.stop()
                        weakT.get()!!.exposeView?.removeAllViews()
                        if (weakT.get()!!.exposeView?.parent != null) {
                            (weakT.get()!!.exposeView?.parent as ViewGroup).removeView(weakT.get()!!.exposeView)
                        }
                    }
                }
            }
            if (sInfoMap[activity.javaClass.name] != null) {
                val bean = sInfoMap[activity.javaClass.name]
                if (bean?.bitmap != null) {
                    bean.bitmap = null
                }
                sInfoMap.remove(activity.javaClass.name)
            }
        }
    }

    private fun show() {
        val bean = sInfoMap[activity?.javaClass?.name] ?: return
        if (showMethod == null) {
            showMethod = NoneShowMethod()
        }
        val parent = activity?.findViewById<View>(Window.ID_ANDROID_CONTENT) as ViewGroup
        //if TranslucentStatus is true , statusBarHeight = 0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (activity?.window?.statusBarColor == 0 || WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS and activity?.window?.attributes!!.flags == WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS) {
                bean.statusBarHeight = 0
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS and activity?.window?.attributes!!.flags == WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS) {
                bean.statusBarHeight = 0
            }
        }
        showMethod!!.showDuration = transitionDuration
        //final ITransitionListener realTransitionListener = transitionListener.get();
        val r = Runnable {
            bean.windowWidth = parent.width
            bean.windowHeight = parent.height
            bean.titleHeight = parent.top
            if (exposeView == null) {
                exposeView = CirleExposeView(activity)
            }
            if (showMethod is InflateShowMethod) {
                exposeView?.inflateBitmap = BitmapUtils.createBitmap((showMethod as InflateShowMethod).inflateView, bean.windowWidth, bean.windowHeight, true)
            }
            exposeView?.setExposeColor(exposeColor, useInflateExpose)
            if (exposeAcceleration > 0) {
                exposeView?.exposeAcceleration = exposeAcceleration
            }
            val params = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT)
            parent.addView(exposeView, params)


            if (targetView != null) {
                //get Target View's position
                targetView?.getGlobalVisibleRect(bean.targetRect)
                bean.targetWidth = bean.targetRect.right - bean.targetRect.left
                bean.targetHeight = bean.targetRect.bottom - bean.targetRect.top
                bean.translationX = bean.originRect.left + bean.originWidth / 2 - bean.targetRect.left - bean.targetWidth / 2
                bean.translationY = bean.originRect.top + bean.originHeight / 2 - bean.targetRect.top - bean.targetHeight / 2
            } else {
                bean.targetRect.left = bean.originRect.left
                bean.targetRect.top = bean.originRect.top
                bean.targetWidth = bean.originWidth
                bean.targetHeight = bean.originHeight
                bean.translationX = 0
                bean.translationY = 0
            }
            //create a temp ImageView to replace origin view
            val ivTemp = ImageView(activity)
            if (bean.bitmap != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    ivTemp.setImageDrawable(BitmapDrawable(bean.bitmap))
                } else {
                    ivTemp.setBackgroundDrawable(BitmapDrawable(bean.bitmap))
                }
            }
            if (bean.originRect.top + bean.originHeight > bean.windowHeight + bean.statusBarHeight + bean.titleHeight) {
                bean.scale = ((bean.windowHeight + bean.statusBarHeight
                        + bean.titleHeight) - bean.originRect.top) as Float / bean.targetHeight
            } else {
                bean.scale = bean.originHeight as Float / bean.targetHeight
            }

            val ivTempParams = RelativeLayout.LayoutParams((bean.targetWidth * bean.scale) as Int,
                    (bean.targetHeight * bean.scale) as Int)
            ivTempParams.setMargins((bean.originRect.left + (bean.originWidth / 2 - bean.targetWidth * bean.scale / 2)) as Int, bean.originRect.top - (parent.top + bean.statusBarHeight), 0, 0)
            bean.translationY = bean.originRect.top + (bean.targetHeight * bean.scale) as Int / 2 - bean.targetRect.top - bean.targetHeight / 2
            bean.translationX = bean.originRect.left + bean.originWidth / 2 - bean.targetRect.left - bean.targetWidth / 2

            exposeView?.addView(ivTemp, ivTempParams)
            showMethod?.reviseInfo(bean)
            showMethod?.translate(bean, exposeView, ivTemp)
            showMethod?.loadPlaceholder(bean, ivTemp)
            showMethod?.set?.addListener(object : AnimatorListenerAdapter() {

                override fun onAnimationEnd(animation: Animator) {
                    exposeView?.mExposeListener = object : ExposeListener {
                        override fun onExposeStart() {
                            if (transitionListener != null) {
                                transitionListener?.onExposeStart()
                            }
                        }

                        override fun onExposeProgrees(progress: Float) {
                            if (transitionListener != null) {
                                transitionListener?.onExposeProgress(progress)
                            }
                        }

                        override fun onExposeEnd() {
                            if (transitionListener != null) {
                                transitionListener?.onExposeEnd()
                            }
                            //recycle
                            if (exposeView != null) {
                                exposeView?.stop()
                                exposeView?.removeAllViews()
                                parent.removeView(exposeView)
                                exposeView = null
                            }
                        }
                    }
                    exposeView?.startExposeAnim(bean)
                    if (targetView != null) {
                        showMethod?.loadTargetView(bean, targetView)
                    }
                }
            })
        }
        parent.post(r)
    }

    class TransitionBuilder(val activity: Activity) {

        var showMethod: ShowMethod? = null

        var transitionListener: TransitionListener? = null

        var transitionDuration = ShowMethod.DEFAULT_DURATION

        var exposeView: ExposeView? = null

        var exposeColor: Int = 0

        var exposeAcceleration: Int = 0

        var useInflateExpose: Boolean = false

        var targetView: View? = null

        fun setExposeView(exposeView: ExposeView): TransitionBuilder {
            this.exposeView = exposeView
            return this
        }

        fun setExposeColor(exposeColor: Int): TransitionBuilder {
            return setExposeColor(exposeColor, false)
        }

        fun setExposeColor(exposeColor: Int, useInflateExpose: Boolean): TransitionBuilder {
            this.exposeColor = exposeColor
            this.useInflateExpose = useInflateExpose
            return this
        }

        fun setShowMethod(showMethod: ShowMethod): TransitionBuilder {
            this.showMethod = showMethod
            return this
        }

        fun intoTargetView(targetView: View): TransitionBuilder {
            this.targetView = targetView
            return this
        }

        fun setTransitionDuration(transitionDuration: Long): TransitionBuilder {
            this.transitionDuration = Math.max(transitionDuration, 0L)
            return this
        }

        fun setTransitionListener(transitionListener: TransitionListener): TransitionBuilder {
            this.transitionListener = transitionListener
            return this
        }

        fun setExposeAcceleration(exposeAcceleration: Int): TransitionBuilder {
            this.exposeAcceleration = exposeAcceleration
            return this
        }

        fun show() {
            val t = TransitionsHeleper.create(this)
            t.show()
        }
    }
}