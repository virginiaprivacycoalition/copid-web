package com.virginiaprivacy.copid.routes

import com.virginiaprivacy.copid.cops
import com.virginiaprivacy.copid.entities.messages
import com.virginiaprivacy.copid.respondWithUser
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.*

fun Route.home() {
    route("/") {
        get {
            call.respondRedirect("/cops/0")
        }
        route("cops/{page}") {
            get {
                if (cops.isEmpty()) {
                    respondWithUser("index.ftl")
                    return@get
                }
                val page: Int = (call.parameters["page"] ?: "0").toInt()
                val pages = cops.chunked(4)
                respondWithUser("index.ftl", "cops" to pages[page], "page" to page, "numPages" to (pages.size - 1),
                    "pages" to IntRange(0, (pages.size - 1)).toList())
            }
        }
    }
}

fun Route.updates() {
    route("/updates") {
        get {
            respondWithUser("updates.ftl", "messages" to messages.sortedByDescending { it.time })
        }
    }
}

fun Route.search() {
    route("/search") {
        post {
            val query = call.parameters["search"]?.escapeHTML() ?: ""
            respondWithUser("searchresults.ftl",
                "results" to cops.filter { it.formattedName.toLowerCase().trim().contains(query.toLowerCase()) })
        }
    }
}



