package com.adrian.simplemvpframe

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.adrian.simplemvpframe.utils.cropBitmap
import com.adrian.simplemvpframe.utils.scaleBmpWithMaxSize
import com.adrian.simplemvpframe.utils.scaleBmpWithMinSize
import kotlinx.android.synthetic.main.activity_bitmap.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import java.io.File

class BitmapActivity : AppCompatActivity() {

    companion object {
        const val TAKE_PHOTO = 1
        const val CHOOSE_PHOTO = 2
    }

    private var uri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bitmap)

        btnMin.onClick {
            val bmp = BitmapFactory.decodeResource(resources, R.drawable.bmp_w)
            if (bmp != null) {
                Log.e("BITMAP", "src w:${bmp?.width} h:${bmp?.height}")
                ivBmp.setImageBitmap(scaleBmpWithMinSize(bmp, 0, 800))
            }
        }
        btnMax.onClick {
            val bmp = BitmapFactory.decodeResource(resources, R.drawable.bmp_h)
            if (bmp != null) {
                ivBmp.setImageBitmap(scaleBmpWithMaxSize(bmp, 400, 0))
            }
        }
        btnCrop.onClick {
            val bmp = BitmapFactory.decodeResource(resources, R.drawable.bmp_n)
            if (bmp != null) {
                ivBmp.setImageBitmap(cropBitmap(bmp, 200, 300, 300, 400))
            }
        }
    }
}
