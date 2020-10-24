package com.virginiaprivacy.copid.entities

import com.virginiaprivacy.copid.usersDir
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File
import java.security.MessageDigest
import java.util.concurrent.ConcurrentHashMap

val userList = ConcurrentHashMap<String, User>()
fun loadUsers() = usersDir.listFiles()?.let { files ->
    files.iterator().forEachRemaining {
        userList[it.name] = Json.decodeFromString(User.serializer(), String(it.readBytes()))
    }
}

abstract class LoginAttempt(val time: Long)

enum class UserType {
    STANDARD,
    ADMIN
}

@Serializable
data class User(
    val name: String,
    val passwordHash: String,
    val type: UserType = UserType.STANDARD
)  {

    fun isAdmin() = type == UserType.ADMIN

    fun save() {
        File(usersDir, name).writeText(Json.encodeToString(serializer(), this))
    }

    companion object {

        fun newUser(username: String, password: String) {
            val user = User(username, password.passHash())
            saveAndRegisterUser(username, user)
        }

        fun newAdmin(username: String, password: String) {
            val user = User(username, password.passHash(), UserType.ADMIN)
            saveAndRegisterUser(username, user)
        }

        private fun saveAndRegisterUser(username: String, user: User) {
            File(usersDir, username).run {
                writeText(Json.encodeToString(serializer(), user))
            }
            userList[username] = user
        }

        private fun createUsersDir(usersDir: File) {
            if (!usersDir.exists()) {
                usersDir.mkdir()
            }
        }

    }
}

fun String.passHash(): String {
    val bytes = this.toByteArray()
    val md = MessageDigest.getInstance("SHA-256")
    val digest = md.digest(bytes)
    return digest.fold("", { str, it -> str + "%02x".format(it) })
}