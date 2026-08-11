package com.gchat.app

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.gchat.app.data.UserManager
import com.gchat.app.databinding.ActivityMainBinding
import com.gchat.app.qr.QRData
import com.gchat.app.qr.QRGenerator

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var userManager: UserManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userManager = UserManager(this)

        setupUser()

        binding.btnScanQR.setOnClickListener {
            startActivity(
                Intent(this, ScannerActivity::class.java)
            )
        }

        binding.btnContacts.setOnClickListener {
            startActivity(
                Intent(this, ContactsActivity::class.java)
            )
        }
    }

    private fun setupUser() {

        val existingUser = userManager.getUser()

        if (existingUser != null) {

            showUser(
                existingUser.name,
                existingUser.id
            )

            binding.btnCreateUser.isEnabled = false
            binding.etName.isEnabled = false
            binding.btnMyQR.isEnabled = true

            setupQRButton(
                existingUser.id,
                existingUser.name
            )

            return
        }

        binding.btnCreateUser.setOnClickListener {

            val name = binding.etName.text
                .toString()
                .trim()

            if (name.isEmpty()) {
                binding.etName.error = "Adını yaz"
                return@setOnClickListener
            }

            val user = userManager.createUser(name)

            showUser(
                user.name,
                user.id
            )

            binding.btnCreateUser.isEnabled = false
            binding.etName.isEnabled = false
            binding.btnMyQR.isEnabled = true

            setupQRButton(
                user.id,
                user.name
            )
        }
    }

    private fun showUser(
        name: String,
        id: String
    ) {

        binding.tvWelcome.text =
            "Xoş gəlmisən, $name!"

        binding.tvUserId.text =
            "@$id"
    }

    private fun setupQRButton(
        id: String,
        name: String
    ) {

        binding.btnMyQR.setOnClickListener {

            val qrData = QRData(
                id = id,
                name = name
            )

            val bitmap = QRGenerator.generate(
                qrData.encode()
            )

            QRActivity.bitmap = bitmap

            startActivity(
                Intent(
                    this,
                    QRActivity::class.java
                )
            )
        }
    }
}
