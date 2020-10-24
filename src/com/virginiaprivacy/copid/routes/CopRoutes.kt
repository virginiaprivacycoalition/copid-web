package com.virginiaprivacy.copid.routes

import com.virginiaprivacy.copid.*
import com.virginiaprivacy.copid.entities.Cop
import com.virginiaprivacy.copid.entities.Photo
import com.virginiaprivacy.copid.entities.save
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.*
import io.ktor.util.pipeline.*
import java.io.File

fun Route.getCop() {
    route("/cop/{lastName}/{firstName}") {
        get {
            val last = call.parameters["lastName"]!!
            val first = call.parameters["firstName"]!!
            val cop = cops.first { it.nameMatch(first, last) }
            respondWithUser("cop.ftl", "cop" to cop)
        }
    }
}

fun Route.getPhoto() {
    route("/cop/{lastName}/{firstName}") {
        route("/photo") {
            authenticate {
                route("/new") {
                    get {
                        val last = call.parameters["lastName"]!!
                        val first = call.parameters["firstName"]!!
                        val cop = cops.first { it.nameMatch(first, last) }
                        respondWithUser("newPhoto.ftl", "cops" to cops, "cop" to cop)

                    }
                    post {
                        val parts = call.receiveMultipart().readAllParts()
                        val last = call.parameters["lastName"]!!
                        val first = call.parameters["firstName"]!!
                        val cop = cops.first { it.nameMatch(first, last) }
                        val photo = parts.filterIsInstance<PartData.FileItem>().first()
                        cop?.let {
                            it.addPhoto(Photo(getFileFromForm(photo).absolutePath))
                            call.respondRedirect(it.url())
                        }

                    }
                }
            }
            route("/{i}") {
                get {
                    val cop = getCopFromName()
                    val index = call.parameters["i"].toString().escapeHTML().toIntOrNull()
                    cop?.run {
                        if (index != null && index >= 0 && index < photos.size) {
                            call.respondFile(photos[index].getFile())
                            return@get
                        }
                        call.respondText("Invalid photo")
                        return@get
                    }
                    call.respondText("invalid cop")
                }
                authenticate {
                    post("/delete") {
                        val cop = getCopFromName()
                        val index = call.parameters["i"].toString().escapeHTML()
                        cop?.run {
                            index.toIntOrNull()?.let {
                                val photo = photos[it]
                                photos.remove(photo)
                                save()
                                photo.getFile().delete()
                                call.respondRedirect("/cop/${lastName}/${firstName}")
                                return@post
                            }
                        }
                        call.respondText("Invalid photo or cop.")
                        return@post
                    }
                }
            }
        }
    }
}

private fun PipelineContext<Unit, ApplicationCall>.getCopFromName(): Cop? {
    val last = call.parameters["lastName"].toString().escapeHTML()
    val first = call.parameters["firstName"].toString().escapeHTML()
    return cops.first { it.nameMatch(first, last) }
}

fun Route.newCop() {
    route("/cop/new") {
        get {
            val template = "newCop.ftl"
            respondWithUser(template)
        }
        authenticate {
            post {
                //                post {
                val parts = call.receiveMultipart().readAllParts()
                log("parts size: ${parts.size}")
                var dirName = ""
                val copInfo = mutableMapOf<String, String>()

                parts
                    .forEach { part ->
                        when (part) {
                            is PartData.FormItem -> {
                                when (part.name) {
                                    "title" -> {
                                        dirName += part.value
                                        copInfo["title"] = part.value
                                    }
                                    "firstName" -> {
                                        copInfo["firstName"] = part.value
                                        dirName += if (dirName.isNotEmpty()) {
                                            "_${part.value}"
                                        } else {
                                            part.value
                                        }
                                    }
                                    "nickName" -> {
                                        copInfo["nickName"] = part.value
                                        with(part.value) {
                                            if (this.isNotEmpty()) {
                                                dirName += "_(${part.value})"
                                            }
                                        }
                                    }
                                    "lastName" -> {
                                        copInfo["lastName"] = part.value
                                        dirName += "_${part.value}"
                                    }
                                }
                                if (part.name == "copID") {
                                    if (!cops.map { it.hashCode().toString() }.contains(part.value)) {
                                        call.respondText("Error: could not find Cop with id ${part.value}")
                                    }
                                    //cop = cops.first { it.id.toString() == part.value }
                                }
                            }
                            is PartData.FileItem -> {
                                val uploadedFile = getFileFromForm(part)
                                val cop = Cop(
                                    copInfo["title"]!!,
                                    copInfo["firstName"]!!,
                                    copInfo["lastName"]!!,
                                    copInfo["nickName"]!!
                                )

                                cop.photos.add(Photo(uploadedFile.path))
                                cops.add(cop)

                                cop.save()
                                Cop.loadAll()

                            }
                            is PartData.BinaryItem -> log("binaryPart: $part")
                        }
                        part.dispose
                    }
                call.respondRedirect("/", false)
                return@post
            }
        }
    }
}

private suspend fun PipelineContext<Unit, ApplicationCall>.getFileFromForm(
    part: PartData.FileItem
): File {
    part.streamProvider()
    val originalFileName = part.originalFileName ?: "uploaded"
    val originalFile = File(originalFileName)
    val ext = originalFile.extension
    checkImageExtension(ext)
    val fileHash = part.hashCode()
    val uploadedFile = File(
        "uploads",
        "${originalFileName + fileHash}.$ext"
    )
    val inputStream = part.streamProvider()
    copyStreamToFile(inputStream, uploadedFile)
    return uploadedFile
}