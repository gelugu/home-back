package com.gelugu.home

import com.gelugu.home.configurations.ApplicationConfig
import io.ktor.server.engine.*
import io.ktor.server.cio.*
import com.gelugu.home.plugins.*
import com.gelugu.home.routing.login.configureLoginRouting
import com.gelugu.home.routing.registration.configureRegistrationRouting
import com.gelugu.home.routing.tasks.configureTasksRouting
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cors.routing.*

fun main() {
    checkEnvironment()

    connectDatabase()

    embeddedServer(
        factory = CIO,
        port = ApplicationConfig.serverPort.toInt(),
        host = "0.0.0.0",
    ) {
        val logger = log

        installCORS(logger)
        installJWT(logger)
        configureSerialization()

        configureRouting()
        configureRegistrationRouting()
        configureLoginRouting()
        configureTasksRouting()
    }.start(wait = true)
}
