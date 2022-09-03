package com.gelugu.home.routing.login

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.gelugu.home.cache.InMemoryCache
import com.gelugu.home.configurations.ApplicationConfig
import com.gelugu.home.database.users.LoginPasswordDTO
import com.gelugu.home.database.users.LoginTelegramDTO
import com.gelugu.home.database.users.Users
import com.gelugu.home.features.TelegramBot
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.*

fun Application.configureLoginRouting() {
  routing {
    fun getToken(userId: String) = JWT.create()
      .withClaim("id", userId)
      .withExpiresAt(Date(System.currentTimeMillis() + ApplicationConfig.tokenExpirationTime))
      .sign(Algorithm.HMAC256(ApplicationConfig.jwtSecret))

    authenticate("jwt") {
      get("/auth/status") {
        call.principal<JWTPrincipal>()?.let {
          call.respondText(it.payload.getClaim("login").asString())
        } ?: call.respond(HttpStatusCode.Unauthorized, "User not exist")
      }

      get("/auth/send-code") {
        val authUser = call.principal<UserIdPrincipal>()
        if (authUser == null) {
          call.respond(HttpStatusCode.Unauthorized, "User not exist")
          return@get
        }

        val user = Users.fetchByLogin(authUser.name)

        if (user.telegram_bot_token.isNotEmpty()) {
          call.respond(HttpStatusCode.Unauthorized, "User ${user.login} has no telegram token")
          return@get
        }

        if (user.telegram_bot_chat_id.isNotEmpty()) {
          call.respond(HttpStatusCode.Unauthorized, "User ${user.login} has no telegram chat id")
          return@get
        }

        val bot = TelegramBot(user.telegram_bot_token, user.telegram_bot_chat_id)

        val code = (1..4)
          .map { ('0'..'9').random() }
          .joinToString("")

        try {
          val responseCode = bot.sendMessage(code)
          if (responseCode.value == 200) {
            call.respond(HttpStatusCode.OK, "Check code in telegram bot")
            InMemoryCache.telegramCodes[user.login] = Pair(code, Date())
            return@get
          } else {
            call.respond(
              HttpStatusCode.BadRequest,
              "Can't send request to telegram bot. Check server logs"
            )
          }
        } catch (e: Exception) {
          call.respond(
            HttpStatusCode.InternalServerError,
            e.message ?: "Can't send request to telegram bot. Check server logs"
          )
        }
      }
    }

    post("/auth/signin/telegram") {
      val user = call.receive<LoginCodeDTO>()

      when {
        user.login.isEmpty() -> {
          val msg = "Login is required"
          call.respond(HttpStatusCode.BadRequest, msg)
          call.application.log.warn(msg)
          return@post
        }
        user.code.isEmpty() -> {
          val msg = "Telegram code is required"
          call.respond(HttpStatusCode.BadRequest, msg)
          call.application.log.warn(msg)
          return@post
        }
      }

      InMemoryCache.telegramCodes[user.login]?.let { code ->
        if (code.second.time + ApplicationConfig.codeExpirationTime < Date().time) {
          call.respond(HttpStatusCode.Unauthorized, "Code expired")
          return@post
        }

        if (user.code != code.first) {
          val msg = "Incorrect login or authorization code"
          call.application.log.error(msg)
          call.respond(HttpStatusCode.Unauthorized, msg)
          return@post
        }

        val token = getToken(user.login)
        call.respond(LoginRespondModel(token))
      } ?: call.respond(HttpStatusCode.Unauthorized, "No authorization code for user ${user.login}")
    }

    post("/auth/signin/password") {
      val user = call.receive<LoginPasswordDTO>()

      when {
        user.login.isEmpty() -> {
          val msg = "Login is required"
          call.respond(HttpStatusCode.BadRequest, msg)
          call.application.log.warn(msg)
          return@post
        }
        user.password.isEmpty() -> {
          val msg = "Password is required"
          call.respond(HttpStatusCode.BadRequest, msg)
          call.application.log.warn(msg)
          return@post
        }
      }

      if (user.password != Users.fetchByLogin(user.login).password) {
        val msg = "Incorrect login or password"
        call.application.log.error(msg)
        call.respond(HttpStatusCode.Unauthorized, msg)
        return@post
      }

      val token = getToken(user.login)
      call.respond(LoginRespondModel(token))
    }
  }
}