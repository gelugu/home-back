package com.gelugu.home.user

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.gelugu.home.configurations.ApplicationConfig
import com.gelugu.home.database.users.dto.UpdateUserDTO
import com.gelugu.home.database.users.Users
import com.gelugu.home.plugins.configureSerialization
import com.gelugu.home.plugins.connectDatabase
import com.gelugu.home.plugins.installJWT
import com.gelugu.home.routing.users.UserProfileDTO
import com.gelugu.home.routing.users.configureUsersRouting
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Test
import java.util.*
import kotlin.test.assertEquals

class ProfileTests {
  private val userId = "098go86fg"
  private val token =  JWT.create()
    .withClaim("id", userId)
    .withExpiresAt(Date(System.currentTimeMillis() + ApplicationConfig.tokenExpirationTime))
    .sign(Algorithm.HMAC256(ApplicationConfig.jwtSecret))
  private val wrongToken =  JWT.create()
    .withClaim("id", "i-am-not-exist")
    .withExpiresAt(Date(System.currentTimeMillis() + ApplicationConfig.tokenExpirationTime))
    .sign(Algorithm.HMAC256(ApplicationConfig.jwtSecret))

  @Test
  fun `Success when user get own profile`() = testApplication {
    connectDatabase()
    application {
      installJWT()
      configureSerialization()
      configureUsersRouting()
    }
    client.get("/profile") {
      contentType(ContentType.Application.Json)
      bearerAuth(token)
    }.apply {
      val user = Users.userToProfile(Users.fetchById(userId))
      assertEquals(HttpStatusCode.OK, status)
      assertEquals(Json.encodeToString(user), bodyAsText())
    }
  }

  @Test
  fun `Error when unknown user get some profile`() = testApplication {
    connectDatabase()
    application {
      installJWT()
      configureSerialization()
      configureUsersRouting()
    }
    client.get("/profile") {
      contentType(ContentType.Application.Json)
      bearerAuth(wrongToken)
    }.apply {
      assertEquals(HttpStatusCode.Unauthorized, status)
      assertEquals(Json.encodeToString("User not found"), bodyAsText())
    }
  }

  @Test
  fun `Error when unauthorized user get some profile`() = testApplication {
    connectDatabase()
    application {
      installJWT()
      configureSerialization()
      configureUsersRouting()
    }
    client.get("/profile") {
      contentType(ContentType.Application.Json)
    }.apply {
      assertEquals(HttpStatusCode.Unauthorized, status)
      assertEquals(Json.encodeToString("Token is not exist, not valid or has expired"), bodyAsText())
    }
  }

  @Test
  fun `Success when user update own profile`() = testApplication {
    connectDatabase()
    application {
      installJWT()
      configureSerialization()
      configureUsersRouting()
    }
    val user = Users.userToProfile(Users.fetchById(userId))
    val expectedUser = UserProfileDTO(
      login = user.login,
      name = user.name,
      telegram_bot_token = user.telegram_bot_token,
      telegram_bot_chat_id = user.telegram_bot_chat_id,
      bio = "new bio",
    )
    client.put("/profile") {
      contentType(ContentType.Application.Json)
      setBody(Json.encodeToString(UpdateUserDTO(bio = "new bio")))
      bearerAuth(token)
    }.apply {
      val updatedUser = Users.userToProfile(Users.fetchById(userId))
      assertEquals(HttpStatusCode.OK, status)
      assertEquals(Json.encodeToString(expectedUser), Json.encodeToString(updatedUser))
    }
  }

  @Test
  fun `Error when unknown user update some profile`() = testApplication {
    connectDatabase()
    application {
      installJWT()
      configureSerialization()
      configureUsersRouting()
    }
    client.put("/profile") {
      contentType(ContentType.Application.Json)
      bearerAuth(wrongToken)
    }.apply {
      assertEquals(HttpStatusCode.Unauthorized, status)
      assertEquals(Json.encodeToString("User not found"), bodyAsText())
    }
  }

  @Test
  fun `Error when unauthorized user update some profile`() = testApplication {
    connectDatabase()
    application {
      installJWT()
      configureSerialization()
      configureUsersRouting()
    }
    client.put("/profile") {
      contentType(ContentType.Application.Json)
    }.apply {
      assertEquals(HttpStatusCode.Unauthorized, status)
      assertEquals(Json.encodeToString("Token is not exist, not valid or has expired"), bodyAsText())
    }
  }

}