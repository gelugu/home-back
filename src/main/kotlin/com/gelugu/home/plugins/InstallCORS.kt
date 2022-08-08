package com.gelugu.home.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.util.logging.*

fun Application.installCORS(logger: Logger) {
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
    allowHost("home.gelugu.com", listOf("http", "https"))
    logger.info("CORS enabled for $hosts")
  }
}