package com.gchat.app.data

import android.content.Context
import com.gchat.app.model.GChatContact
import org.json.JSONArray
import org.json.JSONObject

class ContactManager(context: Context) {

    private val prefs = context.getSharedPreferences(
        "gchat_contacts",
        Context.MODE_PRIVATE
    )

    fun addContact(contact: GChatContact) {

        val contacts = getContacts().toMutableList()

        if (contacts.any { it.id == contact.id }) {
            return
        }

        contacts.add(contact)
        saveContacts(contacts)
    }

    fun getContacts(): List<GChatContact> {

        val json = prefs.getString(
            "contacts",
            "[]"
        ) ?: "[]"

        val array = JSONArray(json)
        val contacts = mutableListOf<GChatContact>()

        for (i in 0 until array.length()) {

            val item = array.getJSONObject(i)

            contacts.add(
                GChatContact(
                    id = item.getString("id"),
                    name = item.getString("name")
                )
            )
        }

        return contacts
    }

    private fun saveContacts(
        contacts: List<GChatContact>
    ) {

        val array = JSONArray()

        contacts.forEach { contact ->

            val item = JSONObject()

            item.put("id", contact.id)
            item.put("name", contact.name)

            array.put(item)
        }

        prefs.edit()
            .putString(
                "contacts",
                array.toString()
            )
            .apply()
    }
}
