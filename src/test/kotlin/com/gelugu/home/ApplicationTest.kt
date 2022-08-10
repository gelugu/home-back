package com.gelugu.home

import com.gelugu.home.cache.InMemoryCache
import io.ktor.http.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlin.test.*
import io.ktor.server.testing.*
import com.gelugu.home.plugins.*
import com.gelugu.home.routing.registration.configureRegistrationRouting
import io.ktor.client.call.*
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

    @Test
    fun `Test success status`() = testApplication {
        application {
            configureRegistrationRouting()
            InMemoryCache.telegramToken = "test"
            InMemoryCache.telegramChatId = "test"
        }
        client.get("/status").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("", bodyAsText())
        }
    }
    @Test
    fun `Test status with no telegram token and chat id`() = testApplication {
        application {
            configureRegistrationRouting()
        }
        client.get("/status").apply {
            assertEquals(HttpStatusCode.InternalServerError, status)
            assertEquals("No telegram bot token", body())
        }
    }
    @Test
    fun `Test status with no telegram token`() = testApplication {
        application {
            configureRegistrationRouting()
            InMemoryCache.telegramChatId = "test"
        }
        client.get("/status").apply {
            assertEquals(HttpStatusCode.InternalServerError, status)
            assertEquals("No telegram bot token", body())
        }
    }
    @Test
    fun `Test status with no telegram chat id`() = testApplication {
        application {
            configureRegistrationRouting()
            InMemoryCache.telegramToken = "test"
        }
        client.get("/status").apply {
            InMemoryCache.telegramToken = "test"
            assertEquals(HttpStatusCode.InternalServerError, status)
            assertEquals("No telegram chat id", body())
        }
    }
}