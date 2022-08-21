package com.gelugu.home.authorization

import com.gelugu.home.database.users.CreateUserDTO
import com.gelugu.home.database.users.Users
import com.gelugu.home.plugins.configureSerialization
import com.gelugu.home.plugins.connectDatabase
import com.gelugu.home.routing.registration.configureRegistrationRouting
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import org.junit.Test
import kotlin.test.assertEquals
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString

open class SignUpTest {
  @Test
  fun `Error when create empty user`() = testApplication {
    application {
      configureSerialization()
      configureRegistrationRouting()
    }
    client.post("/auth/signup") {
      contentType(ContentType.Application.Json)
      val user = CreateUserDTO(
        login = "",
        name = "",
        telegram_bot_token = "",
        telegram_bot_chat_id = "",
        password = "",
      )
      setBody(Json.encodeToString(user))
    }.apply {
      assertEquals(HttpStatusCode.BadRequest, status)
      assertEquals(Json.encodeToString("Login cannot be empty"), bodyAsText())
    }
  }

  @Test
  fun `Error when only login passed`() = testApplication {
    application {
      configureSerialization()
      configureRegistrationRouting()
    }
    val user = CreateUserDTO(
      login = "test-only-login",
      name = "",
      telegram_bot_token = "",
      telegram_bot_chat_id = "",
      password = "",
    )
    client.post("/auth/signup") {
      contentType(ContentType.Application.Json)
      setBody(Json.encodeToString(user))
    }.apply {
      assertEquals(HttpStatusCode.BadRequest, status)
      val msg = "At least on authorization method required (Password or telegram bot integration)"
      assertEquals(Json.encodeToString(msg), bodyAsText())
    }
  }

  @Test
  fun `Error when incorrect telegram bot passed`() = testApplication {}

  @Test
  fun `Error when incorrect telegram chat passed`() = testApplication {}

  @Test
  fun `Error when weak password passed`() = testApplication {
    application {
      configureSerialization()
      configureRegistrationRouting()
    }
    client.post("/auth/signup") {
      contentType(ContentType.Application.Json)
      val user = CreateUserDTO(
        login = "test-weak-user",
        name = "",
        telegram_bot_token = "",
        telegram_bot_chat_id = "",
        password = "123qwerty",
      )
      setBody(Json.encodeToString(user))
    }.apply {
      assertEquals(HttpStatusCode.BadRequest, status)
      val msg = "Password too weak: must includes [digit, lower case letter, upper case letter, special character]"
      assertEquals(Json.encodeToString(msg), bodyAsText())
    }
  }

  @Test
  fun `Error when username not valid`() = testApplication {}

  @Test
  fun `Error when user already exist`() = testApplication {
    connectDatabase()
    application {
      configureSerialization()
      configureRegistrationRouting()
    }
    val user = CreateUserDTO(
      login = "test-exist-user",
      name = "",
      telegram_bot_token = "",
      telegram_bot_chat_id = "",
      password = "n90p3ubf08273y0*&BUL",
    )
    client.post("/auth/signup") {
      contentType(ContentType.Application.Json)
      setBody(Json.encodeToString(user))
    }.apply {}
    client.post("/auth/signup") {
      contentType(ContentType.Application.Json)
      setBody(Json.encodeToString(user))
    }.apply {
      assertEquals(HttpStatusCode.BadRequest, status)
      assertEquals(Json.encodeToString("User with same login already exist"), bodyAsText())
    }
    Users.delete(Users.fetchByLogin(user.login).id)
  }

  @Test
  fun `Success when login and telegram passed`() = testApplication {
    connectDatabase()
    application {
      configureSerialization()
      configureRegistrationRouting()
    }
    val user = CreateUserDTO(
      login = "test-login-telegram-user",
      name = "Test Name",
      telegram_bot_token = "telegram_bot_token",
      telegram_bot_chat_id = "chat_id",
      password = "",
    )
    client.post("/auth/signup") {
      contentType(ContentType.Application.Json)
      setBody(Json.encodeToString(user))
    }.apply {
      val expectedUser = Users.fetchByLogin(user.login)
      assertEquals(HttpStatusCode.Created, status)
      assertEquals(Json.encodeToString(expectedUser), bodyAsText())
      Users.delete(expectedUser.id)
    }
  }

  @Test
  fun `Success when login and password passed`() = testApplication {
    connectDatabase()
    application {
      configureSerialization()
      configureRegistrationRouting()
    }
    val user = CreateUserDTO(
      login = "test-login-password-user",
      name = "Test Name",
      telegram_bot_token = "",
      telegram_bot_chat_id = "",
      password = "n90p3ubf08273y0*&BUL",
    )
    client.post("/auth/signup") {
      contentType(ContentType.Application.Json)
      setBody(Json.encodeToString(user))
    }.apply {
      assertEquals(HttpStatusCode.Created, status)
      val expectedUser = Users.fetchByLogin(user.login)
      assertEquals(Json.encodeToString(expectedUser), bodyAsText())
      Users.delete(expectedUser.id)}
    }

  @Test
  fun `Success when login, telegram and password passed`() = testApplication {
    connectDatabase()
    application {
      configureSerialization()
      configureRegistrationRouting()
    }
    val user = CreateUserDTO(
      login = "test-full-auth-user",
      name = "Test Name",
      telegram_bot_token = "telegram_bot_token",
      telegram_bot_chat_id = "chat_id",
      password = "n90p3ubf08273y0*&BUL",
    )
    client.post("/auth/signup") {
      contentType(ContentType.Application.Json)
      setBody(Json.encodeToString(user))
    }.apply {
      assertEquals(HttpStatusCode.Created, status)
      val expectedUser = Users.fetchByLogin(user.login)
      assertEquals(Json.encodeToString(expectedUser), bodyAsText())
      Users.delete(expectedUser.id)}
    }
}