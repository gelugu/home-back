package com.gelugu.home

import io.ktor.http.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlin.test.*
import io.ktor.server.testing.*
import com.gelugu.home.plugins.*
import org.junit.Test

class ApplicationTest {
    /**
     * Root
     */
    @Test
    fun `Test root`() = testApplication {
        application {
            configureRouting()
        }
        client.get("/").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("Home server started", bodyAsText())
        }
    }
}
