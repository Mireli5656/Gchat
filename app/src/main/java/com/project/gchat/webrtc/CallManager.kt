package com.project.gchat.webrtc

import android.content.Context
import org.webrtc.*

class CallManager(private val context: Context) {
    private var peerConnectionFactory: PeerConnectionFactory? = null
    private var videoCapturer: CameraVideoCapturer? = null
    private var localVideoSource: VideoSource? = null
    private var localAudioSource: AudioSource? = null
    private var localVideoTrack: VideoTrack? = null
    private var localAudioTrack: AudioTrack? = null
    
    // Video qrafikasını ekrana render edən ana motor
    private val rootEglBase: EglBase = EglBase.create()

    init {
        // 1. WebRTC sistemini Android-ə tanıdırıq
        val initializationOptions = PeerConnectionFactory.InitializationOptions.builder(context)
            .createInitializationOptions()
        PeerConnectionFactory.initialize(initializationOptions)

        val options = PeerConnectionFactory.Options()
        peerConnectionFactory = PeerConnectionFactory.builder()
            .setOptions(options)
            .createPeerConnectionFactory()
    }

    // Ekrandakı XML View-ları WebRTC mühərrikinə bağlayırıq
    fun initViews(localView: SurfaceViewRenderer, remoteView: SurfaceViewRenderer) {
        localView.init(rootEglBase.eglBaseContext, null)
        localView.setEnableHardwareScaler(true)
        localView.setMirror(true) // Ön kamera üçün güzgü effekti

        remoteView.init(rootEglBase.eglBaseContext, null)
        remoteView.setEnableHardwareScaler(true)
        remoteView.setMirror(false)
    }

    // Telefonun ön kamerasını tapıb işə salan funksiya
    fun startLocalVideoCapture(localView: SurfaceViewRenderer) {
        val enumerator = Camera2Enumerator(context)
        val deviceNames = enumerator.deviceNames

        // Ön kameranı axtarırıq
        for (deviceName in deviceNames) {
            if (enumerator.isFrontFacing(deviceName)) {
                videoCapturer = enumerator.createCapturer(deviceName, null)
                break
            }
        }

        videoCapturer?.let { capturer ->
            val surfaceTextureHelper = SurfaceTextureHelper.create("CaptureThread", rootEglBase.eglBaseContext)
            localVideoSource = peerConnectionFactory?.createVideoSource(false)
            
            capturer.initialize(surfaceTextureHelper, context, localVideoSource?.capturerObserver)
            capturer.startCapture(1280, 720, 30) // HD keyfiyyət, 30 FPS

            localVideoTrack = peerConnectionFactory?.createVideoTrack("GChat_VideoTrack", localVideoSource)
            localVideoTrack?.addSink(localView)
        }

        // Mikrofonu işə salırıq
        val audioConstraints = MediaConstraints()
        localAudioSource = peerConnectionFactory?.createAudioSource(audioConstraints)
        localAudioTrack = peerConnectionFactory?.createAudioTrack("GChat_AudioTrack", localAudioSource)
    }

    // Zəng bitəndə kameranı və yaddaşı təmizləmək
    fun stopCall() {
        try {
            videoCapturer?.stopCapture()
            videoCapturer?.dispose()
            localVideoSource?.dispose()
            localAudioSource?.dispose()
            peerConnectionFactory?.dispose()
            rootEglBase.release()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
