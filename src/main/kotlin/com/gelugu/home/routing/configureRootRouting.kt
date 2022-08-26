package com.gelugu.home.routing

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRootRouting() {

    routing {
        get("/") {
            call.respond(HttpStatusCode.OK, "Home server started")
        }
    }
}
