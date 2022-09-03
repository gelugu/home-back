package com.gelugu.home

import com.gelugu.home.configurations.ApplicationConfig
import io.ktor.http.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlin.test.*
import io.ktor.server.testing.*
import com.gelugu.home.routing.configureRootRouting
import org.junit.Test

class ApplicationTest {
  /**
   * Root
   */
  @Test
  fun `Test root`() = testApplication {
    application {
      configureRootRouting()
    }
    client.get("/").apply {
      assertEquals(HttpStatusCode.OK, status)
      assertEquals("Home server started", bodyAsText())
    }
  }

  /**
   * Regex
   */
  @Test
  fun `Test regex login`() = testApplication {
    application {
      configureRootRouting()
    }
    client.get("/regex/login").apply {
      assertEquals(HttpStatusCode.OK, status)
      assertEquals(ApplicationConfig.loginRegex.toString(), bodyAsText())
    }
  }

  @Test
  fun `Test regex password`() = testApplication {
    application {
      configureRootRouting()
    }
    client.get("/regex/password").apply {
      assertEquals(HttpStatusCode.OK, status)
      assertEquals(ApplicationConfig.passwordRegex.toString(), bodyAsText())
    }
  }
}
