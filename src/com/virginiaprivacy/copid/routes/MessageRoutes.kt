package com.virginiaprivacy.copid.routes

import com.virginiaprivacy.copid.UserSession
import com.virginiaprivacy.copid.entities.InfoMessage
import com.virginiaprivacy.copid.entities.messages
import com.virginiaprivacy.copid.entities.saveMessages
import com.virginiaprivacy.copid.respondWithUser
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*

fun Route.postMessage() {
            route("message") {
            authenticate {
                get {
                    respondWithUser("message.ftl")
                }
                post {
                    call.sessions.get<UserSession>()?.let {
                        val message = InfoMessage(call.receiveParameters()["content"]?: "", it.username)
                        messages.add(message)
                        saveMessages()
                        call.respondRedirect("/info")
                        return@post
                    }
                    call.respondText("Authorization required")
                }
            }
        }
}