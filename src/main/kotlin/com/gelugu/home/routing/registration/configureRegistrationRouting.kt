package com.gelugu.home.routing.registration

import com.gelugu.home.cache.InMemoryCache
import com.gelugu.home.features.TelegramBot
import com.gelugu.home.routing.exceptions.InternalServerError
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.net.ConnectException

fun Application.configureRegistrationRouting() {
  routing {
    get("/status") {
      if (InMemoryCache.telegramToken.isEmpty()) {
        call.respond(
          HttpStatusCode.InternalServerError,
          "No telegram bot token"
        )
      } else if (InMemoryCache.telegramChatId.isEmpty()) {
        call.respond(
          HttpStatusCode.InternalServerError,
          "No telegram chat id"
        )
      } else {
        call.respond(HttpStatusCode.OK)
        return@get
      }
    }
    post("/register/bot") {
      val token = call.receive<RegistrationTokenModel>().token
      val bot = TelegramBot(token)
      call.application.log.debug("Token: $token")

      try {
        val tgUser = bot.getLastChatId()
        call.application.log.info("Correct telegram token. Last message from \"${tgUser.username}\"")

        InMemoryCache.telegramToken = token
        call.respond(HttpStatusCode.OK, tgUser)

        return@post
      } catch (e: ConnectException) {
        val msg = "ConnectException"
        call.application.log.error(msg)
        call.respond(HttpStatusCode.InternalServerError, InternalServerError(msg))
        e.printStackTrace()
      } catch(e: NullPointerException) {
        val msg = "Can't parse telegram chat id from request"
        call.application.log.error(msg)
        call.respond(HttpStatusCode.InternalServerError, InternalServerError(msg))
        e.printStackTrace()
      } catch (e: java.lang.Exception) {
        val msg = "Unknown server error, please report to https://t.me/gelugu"
        call.application.log.error(msg)
        call.respond(HttpStatusCode.InternalServerError, InternalServerError(msg))
        e.printStackTrace()
      }

      call.respond(
        HttpStatusCode.BadRequest,
        "Incorrect telegram token"
      )
    }
    get("/register/chat") {
      val bot = TelegramBot(InMemoryCache.telegramToken)

      try {
        val tgUser = bot.getLastChatId()
        call.application.log.info("Last message from \"${tgUser.username}\"")

        call.respond(tgUser)
      } catch (e: ConnectException) {
        val msg = "ConnectException"
        call.application.log.error(msg)
        call.respond(HttpStatusCode.InternalServerError, msg)
        e.printStackTrace()
      } catch(e: NullPointerException) {
        val msg = "Can't parse telegram chat id from request"
        call.application.log.error(msg)
        call.respond(HttpStatusCode.InternalServerError, msg)
        e.printStackTrace()
      } catch (e: java.lang.Exception) {
        val msg = "Unknown server error, please report to https://t.me/gelugu"
        call.application.log.error(msg)
        call.respond(HttpStatusCode.InternalServerError, msg)
        e.printStackTrace()
      }
    }
    post("/register/chat") {
      val chat = call.receive<RegistrationChatModel>().chat
      val bot = TelegramBot(InMemoryCache.telegramToken, chat)

      val responseCode = bot.sendMessage("Welcome home")

      if (responseCode == HttpStatusCode.OK) {
        InMemoryCache.telegramChatId = chat

        val msg = "Telegram authorization successfully configured"
        call.application.log.info(msg)
        call.respond(HttpStatusCode.OK, msg)
        return@post
      } else {
        val msg = "Incorrect telegram chat id or telegram token"
        call.application.log.info(msg)
        call.respond(HttpStatusCode.BadRequest, msg)
      }
    }
  }
}
