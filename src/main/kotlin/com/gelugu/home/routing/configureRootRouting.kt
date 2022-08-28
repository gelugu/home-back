package com.gelugu.home.routing

import com.gelugu.home.configurations.ApplicationConfig
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRootRouting() {

    routing {
        get("/") {
            call.respond(HttpStatusCode.OK, "Home server started")
        }

        get("/regex/login") {
            call.respond(HttpStatusCode.OK, ApplicationConfig.loginRegex.toString())
        }
        get("/regex/password") {
            call.respond(HttpStatusCode.OK, ApplicationConfig.passwordRegex.toString())
        }
    }
}
