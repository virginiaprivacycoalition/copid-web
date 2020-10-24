package com.virginiaprivacy.copid

import com.codahale.metrics.MetricRegistry
import com.virginiaprivacy.copid.entities.*
import com.virginiaprivacy.copid.routes.*
import freemarker.cache.ClassTemplateLoader
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.features.*
import io.ktor.freemarker.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.jackson.*
import io.ktor.locations.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*
import io.ktor.util.pipeline.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import org.slf4j.LoggerFactory
import org.slf4j.event.Level
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.CopyOnWriteArraySet
import javax.crypto.spec.SecretKeySpec

data class Prefs(val copsPerPage: Int)
val log = LoggerFactory.getILoggerFactory().getLogger("default")
fun log(any: Any) {
    log.info(any.toString())
}

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

val cops = CopyOnWriteArrayList<Cop>()

val usersDir by lazy {
    File("users")
}

val copsDir by lazy {
    File("cops")
}

val properties = Properties().apply { load(File("build.properties").inputStream()) }


const val sessionKey = "SESSION_LOGIN"

val registry = MetricRegistry()

@KtorExperimentalLocationsAPI
@Suppress("unused") // Referenced in application.conf
fun Application.module() {
    System.load(File("libcopdetector.so").absolutePath)
    Cop.loadAll()
    loadUsers()
    loadMessages()
    install(Authentication) {
        form {
            userParamName = "username"
            passwordParamName = "password"
            challenge { UnauthorizedResponse() }
            skipWhen { call -> call.sessions.get<UserSession>() != null }
            validate { credentials ->
                if (userList[credentials.name]?.passwordHash ?: "" == credentials.password.passHash()) {
                    UserIdPrincipal(credentials.name)
                } else {
                    null
                }
            }
        }
    }
    install(Locations)
    install(DefaultHeaders)
    install(Sessions) {
        cookie<UserSession>(sessionKey) {
            cookie.path = "/"
            transform(
                SessionTransportTransformerMessageAuthentication(
                    SecretKeySpec(
                        "rvarpdcopid".toByteArray(),
                        "HmacSHA256"
                    )
                )
            )
            directorySessionStorage(File(".sessions"), cached = true)
        }
        cookie<Prefs>("prefs") {
            cookie.path = "/"
        }
    }

    install(FreeMarker) {
        templateLoader = ClassTemplateLoader(this::class.java.classLoader, "templates")
    }

    install(CallLogging) {
        level = Level.INFO
        filter { call -> call.request.path().startsWith("/") }
    }

    install(CORS) {
        method(HttpMethod.Options)
        method(HttpMethod.Put)
        method(HttpMethod.Delete)
        method(HttpMethod.Patch)
        header(HttpHeaders.Authorization)
        header("MyCustomHeader")
        allowCredentials = true
        anyHost() // @TODO: Don't do this in production if possible. Try to limit it.
    }

    install(ContentNegotiation) {
        jackson {
        }
    }


    routing {
        home()
        getCop()
        getPhoto()
        newCop()
        updates()
        login()
        logout()
        postMessage()
        search()
        static {
            get("stylesheet.css") {
                call.respondFile(File("uploads/bootstrap.min.css"))
            }
        }

        route("api/") {
            route("/cop/{id}") {
                get {
                    val id = call.parameters["id"]?.toIntOrNull()
                    if (id != null) {
                        cops[id].let {
                            call.respond(it)
                        }
                    }
                }
                authenticate {
                    post {
                        // Sent when deleting a cop
                        val id = call.parameters["id"]?.toIntOrNull()
                        if (id != null && id in cops.map { it.hashCode() }) {
                            call.respondText { "Cop $id deleted: ${cops.first { it.hashCode() == id }.delete()} " }
                            return@post
                        } else {
                            call.respondText { "Cop not found!!" }
                            return@post
                        }
                    }
                }
            }
                get("copids") {
                    call.respond(cops.map { it.hashCode() }.toList())
                }
//                    authenticate {
//                        post {
//                            // Will be sent if someone clicks the Delete button on a photo
//                            val id = call.parameters["id"]?.toIntOrNull()
//                            if (id != null) {
//                                val user = call.sessions.get<UserSession>()
//                                userList[user?.username]?.let { u ->
//                                    cops.filter { cop -> cop.photos.map { it.id }.contains(id) }
//                                        .forEach { copWithPhoto ->
//                                            copWithPhoto.photos.filter { it.id == id }.forEach {
//                                                log.info("User ${u.name} deleted photo with id ${it.id}")
//                                                copWithPhoto.photos.remove(it)
//                                                it.getFile().delete()
//                                            }
//                                        }
//                                    call.respondText("Deleted photo")
//                                    return@post
//                                }
//                                cops.flatMap { it.photos }.find { it.id == id }?.let {
//                                    call.respondFile(it.getPhoto())
//                                }
//                            }
//                        }
//                    }
                }
                get("/cops") {
                    call.respond(cops)
                }
                get("/photos") {
                    call.respond(cops.flatMap { it.photos })
                }
            }
        install(StatusPages) {
            exception<AuthenticationException> { cause ->
                call.respond(HttpStatusCode.Unauthorized)
            }
            exception<AuthorizationException> { cause ->
                call.respond(HttpStatusCode.Forbidden)
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

suspend fun copyStreamToFile(inputStream: InputStream, uploadedFile: File) {
    inputStream.use { input ->
        uploadedFile
            .outputStream()
            .buffered()
            .use {
                input.copyToSuspend(it)
            }
        return@use
    }
}

suspend fun PipelineContext<Unit, ApplicationCall>.checkImageExtension(
    ext: String
) {
    if (!arrayOf(
            "png",
            "jpg",
            "jpeg",
            "bmp",
            "gif"
        ).contains(ext)
    ) {
        call.respondText { "Invalid extension type" }
        error("Invalid extension type")
        // return invalid file  error
    }
}

suspend fun PipelineContext<Unit, ApplicationCall>.respondWithUser(
    templateFile: String,
    vararg data: Pair<String, Any>
) {
    val userSession = call.sessions.get<UserSession>()
    val version = properties["version"]
    val buildDate = properties["buildDate"]
    val statistics = mapOf(
        "version" to version.toString(),
        "buildDate" to buildDate.toString(),
        "copCount" to cops.size,
        "photoCount" to cops.flatMap { it.photos }.size,
        *data
    )
    if (userSession != null) {
        call.respond(FreeMarkerContent(templateFile, mapOf("user" to userList[userSession.username]).plus(statistics)))
    } else {
        call.respond(FreeMarkerContent(templateFile, statistics))
    }

}

suspend fun InputStream.copyToSuspend(
    out: OutputStream,
    bufferSize: Int = DEFAULT_BUFFER_SIZE,
    yieldSize: Int = 4 * 1024 * 1024,
    dispatcher: CoroutineDispatcher = Dispatchers.IO
): Long {
    return withContext(dispatcher) {
        val buffer = ByteArray(bufferSize)
        var bytesCopied = 0L
        var bytesAfterYield = 0L
        while (true) {
            val bytes = read(buffer).takeIf { it >= 0 } ?: break
                out.write(buffer, 0, bytes)

            if (bytesAfterYield >= yieldSize) {
                yield()
                bytesAfterYield %= yieldSize
            }
            bytesCopied += bytes
            bytesAfterYield += bytes
        }
        return@withContext bytesCopied
    }
}

class AuthenticationException : RuntimeException()
class AuthorizationException : RuntimeException()

val messages = CopyOnWriteArraySet<InfoMessage>()