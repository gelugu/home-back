package com.gelugu.home

import com.gelugu.home.configurations.ApplicationConfig
import io.ktor.server.engine.*
import io.ktor.server.cio.*
import com.gelugu.home.plugins.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cors.routing.*
import org.jetbrains.exposed.sql.Database

fun main() {
    Database.connect(
        url = "jdbc:postgresql://${ApplicationConfig.dbUrl}:${ApplicationConfig.dbPort}/${ApplicationConfig.dbName}",
        driver = "org.postgresql.Driver",
        user = ApplicationConfig.dbUser,
        password = ApplicationConfig.dbPassword
    )

    embeddedServer(
        factory = CIO,
        port = ApplicationConfig.serverPort.toInt(),
        host = "0.0.0.0",
    ) {
        install(CORS) {
            allowSameOrigin = true
            allowHost("0.0.0.0:3000")
            allowHeader(HttpHeaders.ContentType)
            allowHeader(HttpHeaders.Accept)
            allowHeader(HttpHeaders.Authorization)
        }
        configureRouting()
        configureRegistrationRouting()
        configureLoginRouting()
        configureTasksRouting()
        configureSerialization()
    }.start(wait = true)
}
