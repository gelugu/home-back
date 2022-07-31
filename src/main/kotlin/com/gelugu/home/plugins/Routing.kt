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
    }
}
