package com.virginiaprivacy.copid.routes

import com.virginiaprivacy.copid.UserSession
import com.virginiaprivacy.copid.sessionKey
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.freemarker.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*

fun Route.login() {
    route("/login") {
        get {
            if (call.sessions.get(sessionKey) != null) {
                call.respondRedirect("/")
                return@get
            }
            call.respond(FreeMarkerContent("login.ftl", "" to ""))
        }
        authenticate {
            post {
                call.principal<UserIdPrincipal>()?.let {
                    call.sessions.set(sessionKey, UserSession(it.name))
                }
                call.respondRedirect("/")
            }
        }
    }
}

fun Route.logout() {
    route("/logout") {
        get {
            call.sessions.clear(sessionKey)
            call.respondRedirect("/")
        }
    }
}