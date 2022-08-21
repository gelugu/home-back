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


      val authValid = user.password.isNotEmpty() || (user.telegram_bot_token.isNotEmpty() && user.telegram_bot_chat_id.isNotEmpty())
      val passwordValid = ApplicationConfig.passwordRegex.matcher(user.password).matches()
      when {
        user.login.isEmpty() -> {
          val msg = "Login cannot be empty"
          call.respond(HttpStatusCode.BadRequest, msg)
          throw BadRequestException(msg)
        }
        !authValid -> {
          val msg = "At least on authorization method required (Password or telegram bot integration)"
          call.respond(HttpStatusCode.BadRequest, msg)
          throw BadRequestException(msg)
        }
        user.password.isNotEmpty() && !passwordValid -> {
          val msg = "Password too weak: must includes [digit, lower case letter, upper case letter, special character]"
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
