package com.gelugu.home.plugins

import com.gelugu.home.cache.InMemoryCache
import com.gelugu.home.configurations.ApplicationConfig
import com.gelugu.home.database.login.LoginRespondModel
import com.gelugu.home.database.login.LoginReceiveModel
import com.gelugu.home.features.TelegramBot
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.*

fun Application.configureLoginRouting() {
  routing {
    get("/send-code") {
      if (InMemoryCache.telegramToken.isEmpty()) {
        call.respond(HttpStatusCode.InternalServerError, "No telegram token")
      }
      if (InMemoryCache.telegramChatId.isEmpty()) {
        call.respond(HttpStatusCode.InternalServerError, "No telegram chat id")
      }

      val bot = TelegramBot(InMemoryCache.telegramToken, InMemoryCache.telegramChatId)

      InMemoryCache.code = (1..4)
        .map { ('0'..'9').random() }
        .joinToString("")
      val responseCode = bot.sendMessage(InMemoryCache.code)

      if (responseCode.value == 200) {
        call.respondText { "Check code in telegram bot" }
        InMemoryCache.codeDate = Date()
        call.application.log.info("New code saved in memory (${InMemoryCache.codeDate})")
        return@get
      } else {
        call.respond(
          HttpStatusCode.BadRequest,
          "Can't send request to telegram bot. Check server logs"
        )
      }
    }
    post("/login") {
      val code = call.receive<LoginReceiveModel>().code

      if (InMemoryCache.codeDate.time + ApplicationConfig.codeExpirationTime < Date().time) {
        val msg = "Code expired"
        call.application.log.error(msg)
        call.respond(HttpStatusCode.Unauthorized, msg)
      }

      if (code == InMemoryCache.code) {
        InMemoryCache.token = UUID.randomUUID().toString()
        InMemoryCache.tokenDate = Date()
        call.application.log.info("New token saved in memory (${InMemoryCache.tokenDate})")
        call.respond(LoginRespondModel(InMemoryCache.token))
        return@post
      } else {
        val msg = "Incorrect authorization code"
        call.application.log.error(msg)
        call.respond(HttpStatusCode.Unauthorized, msg)
      }
    }
  }
}