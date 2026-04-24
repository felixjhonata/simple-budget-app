package com.felixjhonata.simplebudgetapp.shared.util

import android.content.ContentResolver
import android.net.Uri
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.just
import io.mockk.mockk
import org.junit.Test
import java.io.OutputStream

class ExtensionUtilTest {
    @Test
    fun writeToUri_outputStreamIsNotNull_writeWasCalled() {
        // given
        val contentResolver = mockk<ContentResolver>()
        val uri = mockk<Uri>()
        val outputStream = mockk<OutputStream>()

        coEvery { contentResolver.openOutputStream(uri) } returns outputStream
        coEvery { outputStream.write(any<ByteArray>()) } just Runs
        coEvery { outputStream.close() } just Runs

        // when
        contentResolver.writeToUri(uri, "test data")

        // then
        coVerify { contentResolver.openOutputStream(uri) }
        coVerify { outputStream.write(any<ByteArray>()) }
        coVerify { outputStream.close() }

        confirmVerified(contentResolver, uri, outputStream)
    }

    @Test
    fun writeToUri_outputStreamIsNull_writeWasNotCalled() {
        // given
        val contentResolver = mockk<ContentResolver>()
        val uri = mockk<Uri>()

        coEvery { contentResolver.openOutputStream(uri) } returns null

        // when
        contentResolver.writeToUri(uri, "test data")

        // then
        coVerify { contentResolver.openOutputStream(uri) }

        confirmVerified(contentResolver, uri)
    }
}