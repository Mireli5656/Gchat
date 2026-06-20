package com.project.gchat.webrtc

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.webrtc.SurfaceViewRenderer
import com.project.gchat.R

class CallActivity : AppCompatActivity() {
    private lateinit var callManager: CallManager
    private lateinit var localVideoView: SurfaceViewRenderer
    private lateinit var remoteVideoView: SurfaceViewRenderer
    private lateinit var btnHangup: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_call)

        localVideoView = findViewById(R.id.localVideoView)
        remoteVideoView = findViewById(R.id.remoteVideoView)
        btnHangup = findViewById(R.id.btnHangup)

        callManager = CallManager(this)
        callManager.initViews(localVideoView, remoteVideoView)
        callManager.startLocalVideoCapture(localVideoView)

        // Qırmızı düyməyə basanda zəngi bitir
        btnHangup.setOnClickListener {
            callManager.stopCall()
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        callManager.stopCall()
    }
}
