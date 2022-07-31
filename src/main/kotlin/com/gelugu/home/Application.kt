package com.gelugu.home

import com.gelugu.home.configurations.ApplicationConfig
import io.ktor.server.engine.*
import io.ktor.server.cio.*
import com.gelugu.home.plugins.*
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
        install(CORS) {
            allowMethod(HttpMethod.Options)
            allowMethod(HttpMethod.Put)
            allowMethod(HttpMethod.Delete)
            allowMethod(HttpMethod.Patch)
            allowHeader(HttpHeaders.Authorization)
            allowHeader(HttpHeaders.AccessControlAllowOrigin)
            allowNonSimpleContentTypes = true
            allowCredentials = true
            allowSameOrigin = true
            allowHost("localhost:3000", listOf("http"))
            logger.info("CORS enabled for $hosts")
        }
        configureRouting()
        configureRegistrationRouting()
        configureLoginRouting()
        configureTasksRouting()
        configureSerialization()
    }.start(wait = true)
}
