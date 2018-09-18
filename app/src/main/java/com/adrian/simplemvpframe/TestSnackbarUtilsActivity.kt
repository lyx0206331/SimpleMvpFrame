package com.adrian.simplemvpframe

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.Snackbar
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import com.adrian.simplemvpframe.R.id.bt_confirm
import com.adrian.simplemvpframe.views.snackbar.SnackbarUtils
import com.adrian.simplemvpframe.utils.ScreenUtil
import com.adrian.simplemvpframe.utils.SimpleSnackbarUtil
import kotlinx.android.synthetic.main.activity_test_snackbar_utils.*
import org.jetbrains.anko.backgroundColor


class TestSnackbarUtilsActivity : AppCompatActivity(), View.OnClickListener {

//    private var bt_memory: Button? = null
//    private var crazybutton: Button? = null
//    private var bt_short: Button? = null
//    private var bt_long: Button? = null
//    private var bt_indefinite: Button? = null
//    private var bt_length_custom: Button? = null
//    private var bt_info: Button? = null
//    private var bt_confirm: Button? = null
//    private var bt_warn: Button? = null
//    private var bt_danger: Button? = null
//    private var bt_back_custom: Button? = null
//    private var bt_color_message: Button? = null
//    private var bt_color_action: Button? = null
//    private var bt_back_alpha: Button? = null
//    private var bt_action: Button? = null
//    private var bt_callback: Button? = null
//    private var bt_message_gravity_default: Button? = null
//    private var bt_message_gravity_center: Button? = null
//    private var bt_message_gravity_right: Button? = null
//    private var bt_message_leftright_drawable: Button? = null
//    private var bt_addview: Button? = null
//    private var bt_radius: Button? = null
//    private var bt_radius_stroke: Button? = null
//    private var bt_gravity_default: Button? = null
//    private var bt_gravity_top: Button? = null
//    private var bt_gravity_center: Button? = null
//    private var bt_margins: Button? = null
//    private var bt_above: Button? = null
//    private var bt_bellow: Button? = null
//    private var bt_multimethods: Button? = null

    private var instance: SnackbarUtils? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_snackbar_utils)

        crazybutton.setOnClickListener(this)
        bt_memory.setOnClickListener(this)
        //
        bt_short.setOnClickListener(this)
        bt_long.setOnClickListener(this)
        bt_indefinite.setOnClickListener(this)
        bt_length_custom.setOnClickListener(this)
        bt_info.setOnClickListener(this)
        bt_confirm.setOnClickListener(this)
        bt_warn.setOnClickListener(this)
        bt_danger.setOnClickListener(this)
        bt_back_custom.setOnClickListener(this)
        bt_color_message.setOnClickListener(this)
        bt_color_action.setOnClickListener(this)
        bt_back_alpha.setOnClickListener(this)
        bt_action.setOnClickListener(this)
        bt_callback.setOnClickListener(this)
        bt_message_gravity_default.setOnClickListener(this)
        bt_message_gravity_center.setOnClickListener(this)
        bt_message_gravity_right.setOnClickListener(this)
        bt_message_leftright_drawable.setOnClickListener(this)
        bt_addview.setOnClickListener(this)
        bt_radius.setOnClickListener(this)
        bt_radius_stroke.setOnClickListener(this)
        bt_gravity_default.setOnClickListener(this)
        bt_gravity_top.setOnClickListener(this)
        bt_gravity_center.setOnClickListener(this)
        bt_margins.setOnClickListener(this)
        bt_above.setOnClickListener(this)
        bt_bellow.setOnClickListener(this)
        bt_multimethods.setOnClickListener(this)

        instance = SnackbarUtils.Long(bt_confirm, "背景色:confirm").confirm()
    }

    private val handler = Handler()
    var runnable: Runnable = object : Runnable {
        override fun run() {
            Log.e("Jet", "Activity is null?" + (this@TestSnackbarUtilsActivity == null))
            if (this@TestSnackbarUtilsActivity.isDestroyed) {
                Log.e("Jet", "已结销毁了")
                SnackbarUtils().show()
            } else {
                Log.e("Jet", "未销毁,20s后继续尝试")
                finish()
                handler.postDelayed(this, 1000 * 20)
            }
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.bt_memory -> {
                //尝试制造内存泄漏
                finish()
                handler.postDelayed(runnable, 1000 * 20)
            }
            R.id.bt_short ->
                //TODO implement
                SnackbarUtils.Short(bt_short, "显示时长:短+info").info().show()
            R.id.bt_long ->
                //TODO implement
                SnackbarUtils.Long(bt_long, "显示时长:长+info").info().show()
            R.id.bt_indefinite ->
                //TODO implement
                SnackbarUtils.Indefinite(bt_indefinite, "显示时长:无限+info").info().show()
            R.id.bt_length_custom ->
                //TODO implement
                SnackbarUtils.Custom(bt_length_custom, "显示时长:自定义 3秒+info", 1000 * 3).info().show()
            R.id.bt_info ->
                //TODO implement
                SnackbarUtils.Short(bt_info, "背景色:info").info().show()
            R.id.bt_confirm ->
                //TODO implement
                SnackbarUtils.Short(bt_confirm, "背景色:confirm").confirm().show()
            R.id.bt_warn ->
                //TODO implement
                SnackbarUtils.Short(bt_warn, "背景色:warning").warning().show()
            R.id.bt_danger ->
                //TODO implement
                SnackbarUtils.Short(bt_danger, "背景色:danger").danger().show()
            R.id.bt_back_custom ->
                //TODO implement
                SnackbarUtils.Short(bt_back_custom, "背景色:自定义").backColor(-0xee9967).show()
            R.id.bt_color_message ->
                //TODO implement
                SnackbarUtils.Short(bt_color_message, "设置TextView文字颜色").messageColor(Color.RED).show()
            R.id.bt_color_action ->
                //TODO implement
                SnackbarUtils.Short(bt_color_action, "设置Button文字颜色").actionColor(Color.GREEN).setAction("绿色", View.OnClickListener { Toast.makeText(this, "Button文字绿色", Toast.LENGTH_SHORT).show() }).show()
            R.id.bt_back_alpha ->
                //TODO implement
                SnackbarUtils.Short(bt_back_alpha, "设置背景透明度").alpha(0.6f).show()
            R.id.bt_action ->
                //TODO implement
                SnackbarUtils.Short(bt_action, "设置按钮文字及点击监听").setAction("按钮文字", View.OnClickListener { Toast.makeText(this, "点击了按钮!", Toast.LENGTH_SHORT).show() }).show()
            R.id.bt_callback ->
                //TODO implement
                SnackbarUtils.Short(bt_callback, "设置展示及隐藏监听").setCallback(object : Snackbar.Callback() {
                    override fun onDismissed(snackbar: Snackbar, event: Int) {
                        super.onDismissed(snackbar, event)
                        Toast.makeText(this@TestSnackbarUtilsActivity, "onDismissed!", Toast.LENGTH_SHORT).show()
                    }

                    override fun onShown(snackbar: Snackbar) {
                        super.onShown(snackbar)
                        Toast.makeText(this@TestSnackbarUtilsActivity, "onShown!", Toast.LENGTH_SHORT).show()
                    }
                }).show()
            R.id.bt_message_gravity_default ->
                //TODO implement
                SnackbarUtils.Short(bt_message_gravity_default, "文字位置:默认").info().gravityFrameLayout(Gravity.CENTER).show()
            R.id.bt_message_gravity_center ->
                //TODO implement
                SnackbarUtils.Short(bt_message_gravity_center, "文字位置:居中").confirm().gravityFrameLayout(Gravity.CENTER).messageCenter().show()
            R.id.bt_message_gravity_right ->
                //TODO implement
                SnackbarUtils.Short(bt_message_gravity_right, "文字位置:居右").warning().gravityFrameLayout(Gravity.CENTER).messageRight().show()
            R.id.bt_message_leftright_drawable ->
                //TODO implement
                SnackbarUtils.Short(bt_message_leftright_drawable, "设置文字位置左右两侧图片").leftAndRightDrawable(R.mipmap.i9, R.mipmap.i11).show()
            R.id.bt_addview -> {
                //TODO implement
                val imageView = ImageView(this)
                imageView.setImageResource(R.mipmap.ic_launcher)
                SnackbarUtils.Short(bt_addview, "向Snackbar布局中添加View").addView(imageView, 0).show()
            }
            R.id.bt_radius ->
                //TODO implement
                SnackbarUtils.Short(bt_radius, "设置圆角半径值").radius(24f).show()
            R.id.bt_radius_stroke ->
                //TODO implement
                SnackbarUtils.Short(bt_radius_stroke, "设置圆角半径值及边框").radius(30f, 4, Color.GREEN).show()
            R.id.bt_gravity_default ->
                //TODO implement
                SnackbarUtils.Short(bt_gravity_default, "Snackbar位置:默认").show()
            R.id.bt_gravity_top ->
                //TODO implement
                SnackbarUtils.Short(bt_gravity_top, "Snackbar位置:顶部").gravityFrameLayout(Gravity.TOP).show()
            R.id.bt_gravity_center ->
                //TODO implement
                SnackbarUtils.Short(bt_gravity_center, "Snackbar位置:居中").gravityFrameLayout(Gravity.CENTER).show()
            R.id.bt_margins ->
                //TODO implement
                SnackbarUtils.Short(bt_margins, "设置Snackbar布局的外边距").margins(16).show()
            R.id.bt_above -> {
                //TODO implement
                var total = 0
                //计算R.id.Content离屏幕顶端的距离
                //方法一:两者之和(要根据当前Activity是否全屏,是否显示了TitleBar来决定)
                val statusbarHeight = ScreenUtil.getStatusHeight(this)
                val actionbarHeight = ScreenUtil.getActionBarHeight(this)
                //                total = statusbarHeight + actionbarHeight;
                //方法二:在Activity中可以直接获取R.id.Content离屏幕顶端的距离
                val l1 = IntArray(2)
                window.findViewById<View>(android.R.id.content).getLocationInWindow(l1)
                total = l1[1]
                SnackbarUtils.Short(bt_above, "设置Snackbar显示在指定View的上方").above(bt_gravity_center, total, 16, 16).show()
            }
            R.id.bt_bellow -> {
                //TODO implement
//                var total1 = 0
//                val l2 = IntArray(2)
//                window.findViewById<View>(android.R.id.content).getLocationInWindow(l2)
//                total1 = l2[1]
//                SnackbarUtils.Short(bt_bellow, "设置Snackbar显示在指定View的下方").bellow(bt_gravity_center, total1, 32, 32).show()


                SimpleSnackbarUtil.build(bt_margins, "测试一下").setDuration(5000).setRoundCorner(60f).setGravity(Gravity.CENTER)
                        .setStroke(2, Color.GREEN).setBackgroundColor(Color.YELLOW).below(bt_radius_stroke, 160, 160).show()
            }
            R.id.bt_multimethods -> {
                var total2 = 0
                val l3 = IntArray(2)
                window.findViewById<View>(android.R.id.content).getLocationInWindow(l3)
                total2 = l3[1]
                SnackbarUtils.Custom(bt_multimethods, "5s+左右drawable+背景色+圆角带边框+指定View下方", 1000 * 5)
                        .leftAndRightDrawable(R.mipmap.i10, R.mipmap.i11)
                        .backColor(-0x997767)
                        .radius(16f, 4, Color.BLUE)
                        .bellow(bt_margins, total2, 16, 16)
                        .show()
            }
            R.id.crazybutton -> while (true) {
                Log.e("Jet", "测试奔溃前执行次数:" + ++mCount)
                SnackbarUtils.Short(bt_short, "测试内存泄漏$mCount").info().gravityFrameLayout(Gravity.CENTER).show()
            }
        }//                instance.show();
    }

    private var mCount = 0
}
