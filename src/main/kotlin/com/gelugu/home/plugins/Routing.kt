package com.gelugu.home.plugins

import com.gelugu.home.configurations.ApplicationConfig
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {

    routing {
        get("/") {
            call.respond(HttpStatusCode.OK, "Home server started")
        }
        get("/status") {
            if (ApplicationConfig.telegramBotToken.isEmpty()) {
                call.respond(
                    HttpStatusCode.Unauthorized,
                    "No telegram bot token"
                )
                return@get
            }
            if (ApplicationConfig.telegramChatId.isEmpty()) {
                call.respond(
                    HttpStatusCode.Unauthorized,
                    "No telegram chat id"
                )
                return@get
            }

            call.respond(HttpStatusCode.OK)
            return@get
        }
    }
}
