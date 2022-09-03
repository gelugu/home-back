package com.gelugu.home.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.gelugu.home.configurations.ApplicationConfig
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*

fun Application.installJWT() {
  install(Authentication) {

    jwt("jwt") {
      verifier(
        JWT
          .require(Algorithm.HMAC256(ApplicationConfig.jwtSecret))
          .build()
      )
      validate { credential ->
        if (credential.payload.getClaim("id").asString() != "") {
          JWTPrincipal(credential.payload)
        } else {
          null
        }
      }
      challenge { _, _ ->
        call.respond(HttpStatusCode.Unauthorized, "Token is not exist, not valid or has expired")
      }
    }
  }
  log.info("JWT enabled")
}
