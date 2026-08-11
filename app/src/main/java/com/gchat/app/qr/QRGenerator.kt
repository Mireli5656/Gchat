package com.gchat.app.qr

import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter

object QRGenerator {

    fun generate(
        data: String,
        size: Int = 800
    ): Bitmap {

        val matrix = MultiFormatWriter().encode(
            data,
            BarcodeFormat.QR_CODE,
            size,
            size
        )

        val bitmap = Bitmap.createBitmap(
            size,
            size,
            Bitmap.Config.RGB_565
        )

        for (x in 0 until size) {
            for (y in 0 until size) {
                bitmap.setPixel(
                    x,
                    y,
                    if (matrix[x, y]) {
                        Color.BLACK
                    } else {
                        Color.WHITE
                    }
                )
            }
        }

        return bitmap
    }
}
