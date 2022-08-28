package com.gelugu.home.routing.registration

import com.gelugu.home.configurations.ApplicationConfig
import com.gelugu.home.database.users.CreateUserDTO
import com.gelugu.home.database.users.Users
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.exceptions.ExposedSQLException

fun Application.configureRegistrationRouting() {
  routing {
    post("/auth/signup") {
      val user = call.receive<CreateUserDTO>()

      val authValid = user.password.isNotEmpty()
      val passwordValid = ApplicationConfig.passwordRegex.matcher(user.password).matches()
      val loginValid = ApplicationConfig.loginRegex.matcher(user.login).matches()
      when {
        user.login.isEmpty() -> {
          val msg = "Login cannot be empty"
          call.respond(HttpStatusCode.BadRequest, msg)
          throw BadRequestException(msg)
        }
        !loginValid -> {
          val msg = "Login must starts with a letter and contains only letters, numbers, '-' or '_' (3 to 32 symbols)"
          call.respond(HttpStatusCode.BadRequest, msg)
          throw BadRequestException(msg)
        }
        !authValid -> {
          val msg = "At least on authorization method required (Password or telegram bot integration)"
          call.respond(HttpStatusCode.BadRequest, msg)
          throw BadRequestException(msg)
        }
        user.password.isNotEmpty() && !passwordValid -> {
          val msg =
            "Password must contains digit, lower case letter, upper case letter, special character] (8 to 24 symbols)"
          call.respond(HttpStatusCode.BadRequest, msg)
          throw BadRequestException(msg)
        }
      }

      try {
        val id = Users.create(user)
        call.respond(HttpStatusCode.Created, Users.fetchById(id))
      } catch (e: ExposedSQLException) {
        val msg = "User with same login already exist"
        call.respond(HttpStatusCode.BadRequest, msg)
        throw BadRequestException(msg)
      }
    }
  }
}
