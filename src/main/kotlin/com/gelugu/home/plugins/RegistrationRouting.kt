package com.gelugu.home.plugins

import com.gelugu.home.cache.InMemoryCache
import com.gelugu.home.database.registration.RegistrationTokenModel
import com.gelugu.home.database.registration.RegistrationChatModel
import com.gelugu.home.features.TelegramBot
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.net.ConnectException

fun Application.configureRegistrationRouting() {
  routing {
    post("/register/bot") {
      val token = call.receive<RegistrationTokenModel>().token
      val bot = TelegramBot(token)

      try {
        bot.getLastChatId()

        InMemoryCache.telegramToken = token
        call.respond(HttpStatusCode.OK)
        return@post
      } catch (e: ConnectException) {
        call.application.log.error("ConnectException")
        e.printStackTrace()
      } catch (e: java.lang.Exception) {
        call.application.log.error("Unknown server error, please report to https://t.me/gelugu")
        e.printStackTrace()
      }

      call.respond(
        HttpStatusCode.BadRequest,
        "Incorrect telegram token"
      )
    }
    get("/register/chat") {
      val bot = TelegramBot(InMemoryCache.telegramToken)

      call.respond(RegistrationChatModel(bot.getLastChatId()))
    }
    post("/register/chat") {
      val chat = call.receive<RegistrationChatModel>().chat
      val bot = TelegramBot(InMemoryCache.telegramToken, chat)

      val responseCode = bot.sendMessage("Welcome home")

      if (responseCode == HttpStatusCode.OK) {
        InMemoryCache.telegramChatId = chat
        call.respond(HttpStatusCode.OK)
        return@post
      } else {
        call.respond(
          HttpStatusCode.BadRequest,
          "Incorrect telegram token"
        )
      }
    }
  }
}
