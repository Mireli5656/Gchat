package com.gchat.app

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.gchat.app.data.UserManager
import com.gchat.app.databinding.ActivityMainBinding

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

        binding.btnMyQR.setOnClickListener {
            openMyQR()
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

        } else {

            binding.btnMyQR.isEnabled = false
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
            }
        }
    }

    private fun openMyQR() {

        val user = userManager.getUser()

        if (user == null) {

            Toast.makeText(
                this,
                "Əvvəlcə GChat istifadəçisi yarat",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        val intent = Intent(
            this,
            QRActivity::class.java
        )

        intent.putExtra(
            QRActivity.EXTRA_ID,
            user.id
        )

        intent.putExtra(
            QRActivity.EXTRA_NAME,
            user.name
        )

        startActivity(intent)
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
}
