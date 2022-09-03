package com.gelugu.home.authorization

import com.gelugu.home.configurations.ApplicationConfig
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
  fun `Success login with name`() = testApplication {
    connectDatabase()
    application {
      configureSerialization()
      configureRegistrationRouting()
    }
    val user = CreateUserDTO(
      login = "test-login",
      name = "Test Name",
      password = "aaBBccDD112233!_",
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
  fun `Success login without name`() = testApplication {
    connectDatabase()
    application {
      configureSerialization()
      configureRegistrationRouting()
    }
    val user = CreateUserDTO(
      login = "test-login",
      name = "",
      password = "aaBBccDD112233!_",
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
        password = "",
      )
      setBody(Json.encodeToString(user))
    }.apply {
      assertEquals(HttpStatusCode.BadRequest, status)
      assertEquals(Json.encodeToString("Login and password required for registration"), bodyAsText())
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
      password = "",
    )
    client.post("/auth/signup") {
      contentType(ContentType.Application.Json)
      setBody(Json.encodeToString(user))
    }.apply {
      assertEquals(HttpStatusCode.BadRequest, status)
      val msg = "Login and password required for registration"
      assertEquals(Json.encodeToString(msg), bodyAsText())
    }
  }

  @Test
  fun `Error when only name passed`() = testApplication {
    application {
      configureSerialization()
      configureRegistrationRouting()
    }
    val user = CreateUserDTO(
      login = "",
      name = "Test Name",
      password = "",
    )
    client.post("/auth/signup") {
      contentType(ContentType.Application.Json)
      setBody(Json.encodeToString(user))
    }.apply {
      assertEquals(HttpStatusCode.BadRequest, status)
      val msg = "Login and password required for registration"
      assertEquals(Json.encodeToString(msg), bodyAsText())
    }
  }

  @Test
  fun `Error when only password passed`() = testApplication {
    application {
      configureSerialization()
      configureRegistrationRouting()
    }
    val user = CreateUserDTO(
      login = "",
      name = "",
      password = "aaBBccDD112233!_",
    )
    client.post("/auth/signup") {
      contentType(ContentType.Application.Json)
      setBody(Json.encodeToString(user))
    }.apply {
      assertEquals(HttpStatusCode.BadRequest, status)
      val msg = "Login and password required for registration"
      assertEquals(Json.encodeToString(msg), bodyAsText())
    }
  }

  @Test
  fun `Error when weak password passed`() = testApplication {
    application {
      configureSerialization()
      configureRegistrationRouting()
    }
    client.post("/auth/signup") {
      contentType(ContentType.Application.Json)
      val user = CreateUserDTO(
        login = "valid-user",
        name = "",
        password = "123qwerty",
      )
      setBody(Json.encodeToString(user))
    }.apply {
      assertEquals(HttpStatusCode.BadRequest, status)
      val msg = ApplicationConfig.regexExplain[ApplicationConfig.passwordRegex]
      assertEquals(Json.encodeToString(msg), bodyAsText())
    }
  }

  @Test
  fun `Error when login not valid`() = testApplication {
    application {
      configureSerialization()
      configureRegistrationRouting()
    }
    client.post("/auth/signup") {
      contentType(ContentType.Application.Json)
      val user = CreateUserDTO(
        login = "8kk",
        name = "",
        password = "aaBBccDD112233!_",
      )
      setBody(Json.encodeToString(user))
    }.apply {
      assertEquals(HttpStatusCode.BadRequest, status)
      val msg = ApplicationConfig.regexExplain[ApplicationConfig.loginRegex]
      assertEquals(Json.encodeToString(msg), bodyAsText())
    }
  }

  @Test
  fun `Error when user already exist`() = testApplication {
    connectDatabase()
    application {
      configureSerialization()
      configureRegistrationRouting()
    }
    val user = CreateUserDTO(
      login = "test-existing-user",
      name = "",
      password = "aaBBccDD112233!_",
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
}