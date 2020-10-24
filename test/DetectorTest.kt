package com.virginiaprivacy

import java.io.File
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertNotNull

class DetectorTest {

    @BeforeTest
    fun loadNative() {
        System.load(File("libcopdetector.so").absolutePath)
    }

    @Test
    fun `test detector linking and loading`() {
        // Nothing goes here, this will fail if there's a problem loading the native library
    }

    @Test
    fun `test detector instantiates`() {
        println(File("libcopdetector.so").absolutePath)
        val detector = CopDetector()
        assertNotNull(detector)
        detector.delete()
    }
}