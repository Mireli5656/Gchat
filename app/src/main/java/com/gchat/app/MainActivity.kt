package com.gchat.app

import android.os.Bundle
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
    }

    private fun setupUser() {

        val existingUser = userManager.getUser()

        if (existingUser != null) {
            binding.tvWelcome.text =
                "Xoş gəlmisən, ${existingUser.name}!"

            binding.tvUserId.text =
                "@${existingUser.id}"

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

            binding.tvWelcome.text =
                "Xoş gəlmisən, ${user.name}!"

            binding.tvUserId.text =
                "@${user.id}"

            binding.etName.text.clear()

            binding.btnCreateUser.isEnabled = false
        }
    }
}
