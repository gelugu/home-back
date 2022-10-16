package com.gelugu.home.routing.registration

import com.gelugu.home.configurations.ApplicationConfig
import com.gelugu.home.database.users.dto.CreateUserDTO
import com.gelugu.home.database.users.Users
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.exceptions.ExposedSQLException

fun Application.configureRegistrationRouting() {
  routing {
    post("/auth/signup") {
      val user = call.receive<CreateUserDTO>()

      val authInvalid = user.login.isEmpty() || user.password.isEmpty()
      val passwordInvalid = !ApplicationConfig.passwordRegex.matcher(user.password).matches()
      val loginInvalid = !ApplicationConfig.loginRegex.matcher(user.login).matches()

      when {
        authInvalid -> {
          val msg = "Login and password required for registration"
          call.respond(HttpStatusCode.BadRequest, msg)
          call.application.log.warn(msg)
          return@post
        }
        loginInvalid -> {
          ApplicationConfig.regexExplain[ApplicationConfig.loginRegex]?.let {
            call.respond(HttpStatusCode.BadRequest, it)
            call.application.log.warn(it)
            return@post
          } ?: throw Exception("Login regex explaining missed")
        }
        passwordInvalid -> {
          ApplicationConfig.regexExplain[ApplicationConfig.passwordRegex]?.let {
            call.respond(HttpStatusCode.BadRequest, it)
            call.application.log.warn(it)
            return@post
          } ?: throw Exception("Password regex explaining missed")
        }
      }

      try {
        val id = Users.create(user)
        call.respond(HttpStatusCode.Created, Users.fetchById(id))
        call.application.log.info("User created: login(${user.login}) name(${user.name})")
      } catch (e: ExposedSQLException) {
        val msg = "User with same login already exist"
        call.respond(HttpStatusCode.BadRequest, msg)
        call.application.log.warn(msg)
        return@post
      }
    }
  }
}
