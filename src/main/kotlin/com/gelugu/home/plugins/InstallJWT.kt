package com.gelugu.home.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.gelugu.home.configurations.ApplicationConfig
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.util.logging.*

fun Application.installJWT(logger: Logger) {
  install(Authentication) {

    val secret = ApplicationConfig.jwtSecret

    jwt("auth-jwt") {
      verifier(
        JWT
          .require(Algorithm.HMAC256(secret))
          .build()
      )
      validate { credential ->
        if (credential.payload.getClaim("login").asString() != "") {
          JWTPrincipal(credential.payload)
        } else {
          null
        }
      }
      challenge { _, _ ->
        call.respond(HttpStatusCode.Unauthorized, "Token is not valid or has expired")
      }
    }
  }
  logger.info("JWT enabled")
}
