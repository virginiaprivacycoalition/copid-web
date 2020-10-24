package com.virginiaprivacy.copid.routes

import com.virginiaprivacy.copid.Prefs
import com.virginiaprivacy.copid.cops
import com.virginiaprivacy.copid.messages
import com.virginiaprivacy.copid.respondWithUser
import io.ktor.application.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*
import io.ktor.util.*
import kotlinx.css.pre

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
                val prefs = call.sessions.get<Prefs>()
                val perPage = prefs?.copsPerPage ?: 4
                val pages = cops.chunked(perPage)
                respondWithUser("index.ftl", "cops" to pages[page], "page" to page, "numPages" to (pages.size - 1),
                    "pages" to IntRange(0, (pages.size - 1)).toList())
            }
            post {
                val page: Int = (call.parameters["page"] ?: "0").toInt()
                val perPage = call.parameters["copsPerPage"].toString().escapeHTML().toIntOrNull() ?: 4
                call.sessions.set("prefs", Prefs(perPage))
                val pages = cops.chunked(perPage)
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
            call.receiveParameters()["search"]?.let { query ->
                respondWithUser("searchresults.ftl",
                    "results" to cops.filter { it.firstName.toLowerCase().contains(query.toLowerCase())
                            || it.lastName.contains(query.toLowerCase()) })
                return@post
            }
            call.respondText("No results found")
        }
    }
}



