package com.gelugu.home.authorization

import com.gelugu.home.database.users.CreateUserDTO
import com.gelugu.home.database.users.Users
import com.gelugu.home.plugins.configureSerialization
import com.gelugu.home.plugins.connectDatabase
import com.gelugu.home.routing.login.LoginCodeDTO
import com.gelugu.home.routing.login.LoginPasswordDTO
import com.gelugu.home.routing.login.configureLoginRouting
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import org.junit.Test
import kotlin.test.assertEquals
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString

open class SignInTest {
  @Test
  fun `Success when login and password valid`() = testApplication {
    connectDatabase()
    application {
      configureSerialization()
      configureLoginRouting()
    }
    val testUser = Users.fetchById("098go86fg") // from init.sql
    val user = LoginPasswordDTO(
      login = testUser.login,
      password = testUser.password,
    )
    client.post("/auth/signin/password") {
      contentType(ContentType.Application.Json)
      setBody(Json.encodeToString(user))
    }.apply {
      assertEquals(HttpStatusCode.OK, status)
      // ToDo: How to check jwt?
    }
  }

  // ToDo: figure out how to pass telegram sensitive data for tests
  @Test
  fun `Success when login and telegram code valid`() = testApplication {}

  @Test
  fun `Error when no login passed (with password)`() = testApplication {
    application {
      configureSerialization()
      configureLoginRouting()
    }
    val user = LoginPasswordDTO(
      login = "",
      password = "n90p3ubf08273y0*&BUL",
    )
    client.post("/auth/signin/password") {
      contentType(ContentType.Application.Json)
      setBody(Json.encodeToString(user))
    }.apply {
      assertEquals(HttpStatusCode.BadRequest, status)
      assertEquals(Json.encodeToString("Login is required"), bodyAsText())
    }
  }

  @Test
  fun `Error when no login passed (with code)`() = testApplication {
    application {
      configureSerialization()
      configureLoginRouting()
    }
    val user = LoginCodeDTO(
      login = "",
      code = "0000",
    )
    client.post("/auth/signin/telegram") {
      contentType(ContentType.Application.Json)
      setBody(Json.encodeToString(user))
    }.apply {
      assertEquals(HttpStatusCode.BadRequest, status)
      assertEquals(Json.encodeToString("Login is required"), bodyAsText())
    }
  }

  @Test
  fun `Error when no password passed`() = testApplication {
    application {
      configureSerialization()
      configureLoginRouting()
    }
    val user = LoginPasswordDTO(
      login = "mike",
      password = "",
    )
    client.post("/auth/signin/password") {
      contentType(ContentType.Application.Json)
      setBody(Json.encodeToString(user))
    }.apply {
      assertEquals(HttpStatusCode.BadRequest, status)
      assertEquals(Json.encodeToString("Password is required"), bodyAsText())
    }
  }

  @Test
  fun `Error when no telegram code`() = testApplication {
    application {
      configureSerialization()
      configureLoginRouting()
    }
    val user = LoginCodeDTO(
      login = "test-user",
      code = "",
    )
    client.post("/auth/signin/telegram") {
      contentType(ContentType.Application.Json)
      setBody(Json.encodeToString(user))
    }.apply {
      assertEquals(HttpStatusCode.BadRequest, status)
      assertEquals(Json.encodeToString("Telegram code is required"), bodyAsText())
    }
  }

  // ToDo: figure out how to pass telegram sensitive data for tests
  @Test
  fun `Error when telegram authorization not configured`() = testApplication {}
  @Test
  fun `Error when telegram code wrong`() = testApplication {}
  @Test
  fun `Telegram code expiring when it wrong for 3 times`() = testApplication {}
}