package com.gelugu.home

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

open class SignInTest {
  @Test
  fun `Error when no login passed`() = testApplication {}

  @Test
  fun `Error when no telegram code passed`() = testApplication {}

  @Test
  fun `Error when no password passed`() = testApplication {}

  @Test
  fun `Error when code wrong`() = testApplication {}

  @Test
  fun `Remove code when it wrong for 3 times`() = testApplication {}

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