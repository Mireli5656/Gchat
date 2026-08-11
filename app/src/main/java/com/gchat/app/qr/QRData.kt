package com.gchat.app.qr

data class QRData(
    val id: String,
    val name: String
) {
    fun encode(): String {
        return "GCHAT|1|$id|$name"
    }

    companion object {
        fun decode(data: String): QRData? {
            val parts = data.split("|")

            if (parts.size != 4) return null
            if (parts[0] != "GCHAT") return null
            if (parts[1] != "1") return null

            return QRData(
                id = parts[2],
                name = parts[3]
            )
        }
    }
}
