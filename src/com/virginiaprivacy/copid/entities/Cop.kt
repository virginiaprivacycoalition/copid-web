package com.virginiaprivacy.copid.entities

import com.virginiaprivacy.copid.cops
import com.virginiaprivacy.copid.copsDir
import io.ktor.locations.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File
import java.util.*
import kotlin.system.measureTimeMillis

@Serializable
data class Photo(val path: String)
{
    fun getFile() = File(path)
}

@Location("/cop/{firstName}/{lastName}")
@Serializable
data class Cop(
        val title: String,
        val firstName: String,
        val lastName: String,
        val nickName: String = "",
        val photos: MutableList<Photo> = mutableListOf(),
        ) {
    fun getDisplayPhoto() = photos.random()
    val formattedName: String
    get() = getFormattedCopName(this)

    fun addPhoto(vararg photo: Photo) {
        val time = measureTimeMillis {
            Collections.synchronizedList(photos).addAll(photo)
            save()
        }
    }

    fun photoUrl(copPhoto: Photo): String? {
        if (!photos.contains(copPhoto)) {
            return null
        }
        return url() + "/photo/${photos.indexOf(copPhoto)}"
    }

    fun url() = "/cop/${lastName.normalize()}/${firstName.normalize()}"

    fun delete(): Boolean {
        cops.remove(this)
        println("Deleted cop with id ${hashCode()}!")
        return File(copsDir, hashCode().toString()).delete()
    }

    fun nameMatch(first: String, last: String): Boolean {
        return firstName.normalize() == first.normalize() && lastName.normalize() == last.normalize()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Cop
        if (firstName.normalize() == other.firstName.normalize() && lastName.normalize() == other.lastName.normalize()) {
            return true
        }
        return false
    }

    override fun hashCode(): Int {
        var result = 0
        result = 31 * result + firstName.hashCode()
        result = 31 * result + lastName.hashCode()
        return result
    }


    companion object {

        @Deprecated("Old method of loading cops. Use Cop.loadAll()")
        private fun getNameFromFolder(name: String): List<String> {
            return when (name.count { it == '_' }) {
                3 -> {
                    name.split("_", limit = 4).run {
                        this
                    }
                }
                2 -> name.split("_", limit = 3).toMutableList().apply { add(0, "") }
                else -> name.split("_", limit = 2).toMutableList().apply {
                    add(0, "")
                    add(2, "")
                }

            }
        }

        fun loadAll() {
            val json = Json { ignoreUnknownKeys = true }
            cops.clear()
            copsDir.walkTopDown().maxDepth(1).forEach {
                if (!it.isDirectory) {
                    cops.add(json.decodeFromString(serializer(), String(it.readBytes())))
                }
            }
           println("Loaded ${cops.size} cops")
        }


        fun getFormattedCopName(cop: Cop): String {
            var output = ""
            cop.title.capitalize(Locale.ROOT).run {
                if (this.isNotEmpty()) {
                    output += "${this}. "
                }
            }
            cop.firstName.capitalize(Locale.ROOT).run {
                if (this.isNotEmpty()) {
                    output += if (output.isNotEmpty()) {
                        this
                    } else {
                        " $this"
                    }
                }
            }
            cop.nickName.run {
                if (this.isNotEmpty() && cop.nickName.replace("\"", "").isNotEmpty()) {
                    output += " (${this.replace("\"", "").capitalize(Locale.getDefault())}) "
                }
            }
            cop.lastName.capitalize(Locale.getDefault()).run {
                if (this.isNotEmpty()) {
                    output += " $this"
                }
            }
            return output
        }
    }
}

fun Cop.save() {
    println(
        "Saving cop took " + measureTimeMillis {

            File(copsDir, this.hashCode().toString()).writeText(Json.encodeToString(Cop.serializer(), this))
        } / 1000.0 + " seconds"
    )
}

fun String.normalize(): String {
    return this.toLowerCase().trim()
}