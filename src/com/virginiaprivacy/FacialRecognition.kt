package com.virginiaprivacy

import com.google.common.base.Stopwatch
import com.virginiaprivacy.copid.entities.Cop
import com.virginiaprivacy.copid.log
import kotlinx.coroutines.*
import java.io.File
import java.time.Duration
import java.util.concurrent.CopyOnWriteArrayList

object FacialRecognition {

    private val scope = CoroutineScope(newSingleThreadContext("detector"))

//    val requestQueue = ConcurrentLinkedQueue<IdentificationRequest>()

    val executionTimes = CopyOnWriteArrayList<Duration>()

    fun identifyImage(image: File): Deferred<String> {
        return scope.async {
            val timer = Stopwatch.createStarted()
            val result = detector.identifyImage(image.absolutePath)
            executionTimes.add(timer.elapsed())
            result
        }
    }

    suspend fun trainImage(image: File, cop: Cop) {
        val timer = Stopwatch.createStarted()
        if (hasFaces(image).await()) {
            detector.createPolice(
                image.absolutePath,
                "${cop.firstName.toLowerCase()} ${cop.lastName.toLowerCase()}"
            )
            executionTimes.add(timer.elapsed())
        }
    }

    fun hasFaces(image: File): Deferred<Boolean> {
        return scope.async {
            val timer = Stopwatch.createStarted()
            val result = with(detector.countFaces(image.absolutePath)) {
                return@with this > 0
            }
            executionTimes.add(timer.elapsed())
            return@async result
        }
    }

    fun numFaces(image: File): Deferred<Int> {
        return scope.async {
            detector.countFaces(image.absolutePath)
        }
    }

    fun saveModel() {
        detector.save()
    }

    fun loadModel() {
        if (File("cops.dat").exists()) {
            detector.load()
        }
        else {
            log.error("No serialized model exists")
        }
    }

    private val detector by lazy {
        CopDetector()
    }

}

fun main() {
    System.load(File("libcopdetector.so").absolutePath)
    runBlocking {
        println(
            FacialRecognition.hasFaces(File("1.jpeg")).await()
        )
    }
}

