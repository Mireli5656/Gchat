package com.gchat.app.data

import android.content.Context
import com.gchat.app.model.GChatUser
import java.util.UUID

class UserManager(context: Context) {

    private val prefs = context.getSharedPreferences(
        "gchat_user",
        Context.MODE_PRIVATE
    )

    fun createUser(name: String): GChatUser {
        val id = prefs.getString("id", null)
            ?: "gchat_${UUID.randomUUID().toString().take(8)}"

        val user = GChatUser(
            id = id,
            name = name
        )

        prefs.edit()
            .putString("id", user.id)
            .putString("name", user.name)
            .apply()

        return user
    }

    fun getUser(): GChatUser? {
        val id = prefs.getString("id", null) ?: return null
        val name = prefs.getString("name", null) ?: return null

        return GChatUser(
            id = id,
            name = name
        )
    }
}
