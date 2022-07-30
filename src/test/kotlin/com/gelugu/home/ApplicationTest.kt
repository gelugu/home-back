package com.gelugu.home

import com.gelugu.home.configurations.ApplicationConfig
import io.ktor.server.routing.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlin.test.*
import io.ktor.server.testing.*
import com.gelugu.home.plugins.*
import org.junit.Test

class ApplicationTest {
    @Test
    fun testRoot() = testApplication {
        application {
            configureRouting()
        }
        client.get("/").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("Home server started", bodyAsText())
        }
    }

    @Test
    fun testStatus() = testApplication {
        application {
            configureRouting()
        }
        client.get("/status").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("", bodyAsText())
        }
        client.get("/status").apply {
            assertEquals(HttpStatusCode.Unauthorized, status)
            assertEquals("No telegram bot token", bodyAsText())
        }
        client.get("/status").apply {
            assertEquals(HttpStatusCode.Unauthorized, status)
            assertEquals("No telegram chat id", bodyAsText())
        }
    }

    @Test
    fun testLogin() = testApplication {
        application {
            configureRouting()
        }
        client.get("/").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("Home server started", bodyAsText())
        }
    }

    @Test
    fun testRegistration() = testApplication {
        application {
            configureRouting()
        }
        client.get("/").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("Home server started", bodyAsText())
        }
    }

    @Test
    fun testTasks() = testApplication {
        application {
            configureRouting()
        }
        client.get("/").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("Home server started", bodyAsText())
        }
    }
}