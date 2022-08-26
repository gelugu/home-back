package com.gelugu.home

import com.gelugu.home.database.users.LoginPasswordDTO
import com.gelugu.home.plugins.configureSerialization
import com.gelugu.home.plugins.connectDatabase
import com.gelugu.home.plugins.installJWT
import io.ktor.http.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlin.test.*
import io.ktor.server.testing.*
import com.gelugu.home.routing.configureRootRouting
import com.gelugu.home.routing.login.LoginRespondModel
import com.gelugu.home.routing.login.configureLoginRouting
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
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

  @Test
  fun `Test status`() = testApplication {
    connectDatabase()
    application {
      installJWT()
      configureSerialization()

      configureLoginRouting()
    }

    val user = LoginPasswordDTO("mike", "iuLuho87Go86f8&!")
    var token = ""

    client.post("/signin/password") {
      contentType(ContentType.Application.Json)
      setBody(Json.encodeToString(user))
    }.apply {
      token = Json.decodeFromString<LoginRespondModel>(bodyAsText()).token
    }
    client.get("/status") {
      bearerAuth(token)
    }.apply {
      assertEquals(HttpStatusCode.OK, status)
      assertEquals(user.login, bodyAsText())
    }
  }
}
