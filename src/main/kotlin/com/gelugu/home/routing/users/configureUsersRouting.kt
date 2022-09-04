package com.gelugu.home.routing.users

import com.gelugu.home.database.users.UpdateUserDTO
import com.gelugu.home.database.users.Users
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureUsersRouting() {
  routing {

    authenticate("jwt") {

      get("/profile") {
        Users.getUserIdFromJWT(call)?.let { userId ->
          call.respond(HttpStatusCode.OK, Users.userToProfile(Users.fetchById(userId)))
        }
      }

      put("/profile") {
        Users.getUserIdFromJWT(call)?.let { userId ->
          val user = call.receive<UpdateUserDTO>()
          Users.update(userId, user)
          call.respond(HttpStatusCode.OK, Users.userToProfile(Users.fetchById(userId)))
        }
      }

    }
  }
}