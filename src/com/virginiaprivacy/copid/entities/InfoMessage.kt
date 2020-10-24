package com.virginiaprivacy.copid.entities

import com.virginiaprivacy.copid.messages
import io.ktor.http.*
import io.ktor.util.date.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import java.io.File
import java.time.Instant

@Serializable
data class InfoMessage(val content: String, val addedBy: String) {

    val time = System.currentTimeMillis()

    fun timePosted() = Instant.ofEpochMilli(time).toGMTDate().toHttpDate()

}

val messagesFile: File
get() {
    with(File("messages")) {
        if (!exists ()) {
            createNewFile()
        }
        return this
    }
}

private val messagesSerializer = ListSerializer(InfoMessage.serializer())

fun loadMessages() {
    messages.addAll(Json.decodeFromString(
        messagesSerializer,
            String(messagesFile.readBytes())))
}
fun saveMessages() {
    messagesFile.writeText(Json.encodeToString(messagesSerializer, messages.toList()))
}