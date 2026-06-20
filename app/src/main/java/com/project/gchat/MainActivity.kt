package com.project.gchat

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.project.gchat.crypto.CryptoManager
import com.project.gchat.qr.QRScannerActivity
import com.project.gchat.webrtc.CallActivity
import javax.crypto.SecretKey
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var tvKeyStatus: TextView
    private lateinit var btnShowQR: Button
    private lateinit var btnScanQR: Button
    private lateinit var btnVideoCall: Button
    private lateinit var tvChatLog: TextView
    private lateinit var etMessage: EditText
    private lateinit var btnSend: ImageButton
    private lateinit var btnAttachFile: ImageButton

    private var mySecretKey: SecretKey? = null
    private var activeSecretKey: SecretKey? = null

    // QR Tarayıcıdan dönen şifreleme anahtarını yakalayan dinleyici
    private val qrScanLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val scannedKeyString = result.data?.getStringExtra("SCAN_RESULT")
            if (!scannedKeyString.isNullOrEmpty()) {
                try {
                    activeSecretKey = CryptoManager.stringToKey(scannedKeyString)
                    tvKeyStatus.text = "Durum: KORUMALI BAĞLANTI AKTİF 🟢"
                    tvKeyStatus.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_dark))
                    btnVideoCall.isEnabled = true
                    appendMessage("Sistem", "QR Anahtarı eşleşti! Artık her şey AES-256 ile şifreleniyor.")
                } catch (e: Exception) {
                    Toast.makeText(this, "Geçersiz QR Anahtarı!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Dosya seçici dinleyicisi
    private val filePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            if (activeSecretKey == null) {
                Toast.makeText(this, "Önce dostunuzun QR kodunu okutun!", Toast.LENGTH_SHORT).show()
                return@let
            }
            // Dosya yolunu şifreliyoruz
            val encryptedPair = CryptoManager.encrypt("FILE_URI:${it}", activeSecretKey!!)
            appendMessage("Ben (Şifreli Dosya)", "[Ağ Paketi: ${encryptedPair.first.take(12)}...]")
            
            // Karşı tarafın deşifre ettiğini simüle edelim:
            val decrypted = CryptoManager.decrypt(encryptedPair.first, encryptedPair.second, activeSecretKey!!)
            appendMessage("Dost (Deşifre Edilen)", "Gelen Dosya: $decrypted")
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.CAMERA] != true || permissions[Manifest.permission.RECORD_AUDIO] != true) {
            Toast.makeText(this, "Kamera ve Mikrofon izni vermeden GChat çalışamaz.", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvKeyStatus = findViewById(R.id.tvKeyStatus)
        btnShowQR = findViewById(R.id.btnShowQR)
        btnScanQR = findViewById(R.id.btnScanQR)
        btnVideoCall = findViewById(R.id.btnVideoCall)
        tvChatLog = findViewById(R.id.tvChatLog)
        etMessage = findViewById(R.id.etMessage)
        btnSend = findViewById(R.id.btnSend)
        btnAttachFile = findViewById(R.id.btnAttachFile)

        // Uygulama açıldığı an benzersiz şifre anahtarımızı üretiyoruz
        mySecretKey = CryptoManager.generateSecretKey()

        requestPermissions()
        setupListeners()
    }

    private fun setupListeners() {
        // 1. "Anahtarım" Butonu: Kendi anahtarımızı QR koda dönüştürüp ekrana verir
        btnShowQR.setOnClickListener {
            mySecretKey?.let { key ->
                val keyStr = CryptoManager.keyToString(key)
                // Sıfır kütüphane yüküyle QR çizdirme hilesi:
                val qrApiUrl = "https://api.qrserver.com/v1/create-qr-code/?size=350x350&data=${Uri.encode(keyStr)}"

                AlertDialog.Builder(this)
                    .setTitle("Kişisel QR Anahtarınız")
                    .setMessage("Dostunuz kendi GChat uygulamasından 'QR Okut' butonuna basıp aşağıdaki linkten açılacak QR kodu kamerasına göstermelidir.\n\nKod: ${keyStr.take(15)}...")
                    .setPositiveButton("QR Kodu Aç") { _, _ ->
                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(qrApiUrl)))
                    }
                    .setNegativeButton("Kapat", null)
                    .show()
            }
        }

        // 2. QR Okut Butonu
        btnScanQR.setOnClickListener {
            qrScanLauncher.launch(Intent(this, QRScannerActivity::class.java))
        }

        // 3. Görüntülü Arama Butonu
        btnVideoCall.setOnClickListener {
            startActivity(Intent(this, CallActivity::class.java))
        }

        // 4. Mesaj Gönderme
        btnSend.setOnClickListener {
            val text = etMessage.text.toString().trim()
            if (text.isEmpty()) return@setOnClickListener

            if (activeSecretKey == null) {
                Toast.makeText(this, "Önce bir QR kod eşleştirin!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val encrypted = CryptoManager.encrypt(text, activeSecretKey!!)
            val time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())

            appendMessage("Ben ($time)", "🔒 ${encrypted.first.take(16)}... (Şifreli)")
            
            // Karşı telefonun kodu çözdüğünü ekranda test edelim:
            val decryptedText = CryptoManager.decrypt(encrypted.first, encrypted.second, activeSecretKey!!)
            appendMessage("Dost ($time)", decryptedText)

            etMessage.text.clear()
        }

        // 5. Dosya Yükleme
        btnAttachFile.setOnClickListener {
            filePickerLauncher.launch("*/*")
        }
    }

    private fun appendMessage(sender: String, msg: String) {
        tvChatLog.text = "${tvChatLog.text}\n• $sender:\n  $msg\n"
    }

    private fun requestPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO))
        }
    }
}
