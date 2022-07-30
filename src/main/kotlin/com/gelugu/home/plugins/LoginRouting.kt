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
      val bot = TelegramBot(ApplicationConfig.telegramBotToken, ApplicationConfig.telegramChatId)

      InMemoryCache.code = (1..4)
        .map { ('0'..'9').random() }
        .joinToString("")
      val responseCode = bot.sendMessage(InMemoryCache.code)

      if (responseCode.value == 200) {
        call.respondText { "Check code in telegram bot" }
      } else {
        call.respond(
          HttpStatusCode.BadRequest,
          "Can't send request to telegram bot. Check server logs"
        )
      }

      return@get
    }
    post("/login") {
      val code = call.receive<LoginReceiveModel>().code
      if (code == InMemoryCache.code) {
        InMemoryCache.token = UUID.randomUUID().toString()
        call.respond(LoginRespondModel(InMemoryCache.token))
        return@post
      } else {
        call.respond(
          HttpStatusCode.Unauthorized,
          "Incorrect authorization code"
        )
      }
    }
  }
}