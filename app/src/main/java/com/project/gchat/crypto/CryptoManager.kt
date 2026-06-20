package com.project.gchat.crypto

import android.util.Base64
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

object CryptoManager {
    private const val AES_MODE = "AES/GCM/NoPadding"
    private const val KEY_SIZE = 256

    // 1. QR kod vasitəsilə paylaşmaq üçün yeni gizli açar yaradırıq
    fun generateSecretKey(): SecretKey {
        val keyGenerator = KeyGenerator.getInstance("AES")
        keyGenerator.init(KEY_SIZE)
        return keyGenerator.generateKey()
    }

    // 2. Açarı mətnə (String) çeviririk ki, QR kodda rahat oxunsun
    fun keyToString(secretKey: SecretKey): String {
        return Base64.encodeToString(secretKey.encoded, Base64.NO_WRAP)
    }

    // 3. QR koddan oxunan mətni yenidən açara çeviririk
    fun stringToKey(keyString: String): SecretKey {
        val decodedKey = Base64.decode(keyString, Base64.NO_WRAP)
        return SecretKeySpec(decodedKey, 0, decodedKey.size, "AES")
    }

    // 4. Mesajı, fayl linkini və ya videonu şifrələmək
    fun encrypt(data: String, secretKey: SecretKey): Pair<String, String> {
        val cipher = Cipher.getInstance(AES_MODE)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        val encryptedBytes = cipher.doFinal(data.toByteArray(Charsets.UTF_8))

        val encryptedText = Base64.encodeToString(encryptedBytes, Base64.NO_WRAP)
        val ivText = Base64.encodeToString(cipher.iv, Base64.NO_WRAP)
        return Pair(encryptedText, ivText) // Şifrəli mətn və IV (vektor) qaytarır
    }

    // 5. Şifrəli mesajı oxumaq (Deşifrə)
    fun decrypt(encryptedText: String, ivText: String, secretKey: SecretKey): String {
        val cipher = Cipher.getInstance(AES_MODE)
        val iv = Base64.decode(ivText, Base64.NO_WRAP)
        val spec = GCMParameterSpec(128, iv)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, spec)

        val decodedBytes = Base64.decode(encryptedText, Base64.NO_WRAP)
        val decryptedBytes = cipher.doFinal(decodedBytes)
        return String(decryptedBytes, Charsets.UTF_8)
    }
}
