package com.gchat.app

import android.graphics.Bitmap
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class QRActivity : AppCompatActivity() {

    companion object {
        var bitmap: Bitmap? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val imageView = ImageView(this)

        imageView.layoutParams = android.view.ViewGroup.LayoutParams(
            android.view.ViewGroup.LayoutParams.MATCH_PARENT,
            android.view.ViewGroup.LayoutParams.MATCH_PARENT
        )

        imageView.setPadding(32, 32, 32, 32)
        imageView.scaleType = ImageView.ScaleType.CENTER_INSIDE

        imageView.setImageBitmap(bitmap)

        setContentView(imageView)
    }

    override fun onDestroy() {
        bitmap = null
        super.onDestroy()
    }
}
