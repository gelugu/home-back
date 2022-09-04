package com.gelugu.home.database.users

import com.gelugu.home.routing.users.UserProfileDTO
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

object Users : Table() {
  private val id = Users.varchar("id", 64)
  private val login = Users.varchar("login", 64)
  private val name = Users.varchar("name", 64).default("")
  private val telegram_bot_token = Users.varchar("telegram_bot_token", 64).default("")
  private val telegram_bot_chat_id = Users.varchar("telegram_bot_chat_id", 64).default("")
  private val password = Users.varchar("password", 64).default("")
  private val bio = Users.text("bio").default("")

  fun create(userDTO: CreateUserDTO): String {
    val userId = UUID.randomUUID().toString()
    transaction {
      Users.insert { user ->
        user[id] = userId
        user[login] = userDTO.login
        user[name] = userDTO.name
        user[password] = userDTO.password
      }
    }
    return userId
  }

  fun update(userId: String, userDTO: UpdateUserDTO) {
    return transaction {
      Users.update({ Users.id eq userId }) { user ->
        userDTO.login?.let { user[login] = it }
        userDTO.name?.let { user[name] = it }
        userDTO.telegram_bot_token?.let { user[telegram_bot_token] = it }
        userDTO.telegram_bot_chat_id?.let { user[telegram_bot_chat_id] = it }
        userDTO.password?.let { user[password] = it }
        userDTO.bio?.let { user[bio] = it }
      }
    }
  }

  fun delete(userId: String) {
    transaction {
      // delete all users tasks and other staff
      Users.deleteWhere { Users.id eq userId }
    }
  }

  fun fetchAll(): List<UserDTO> {
    return transaction {
      Users.selectAll().map { rowToTask(it) }
    }
  }

  fun fetchById(id: String): UserDTO {
    return transaction {
      Users.select { Users.id eq id }.limit(1).single().let { rowToTask(it) }
    }
  }
  fun fetchByLogin(login: String): UserDTO {
    return transaction {
      Users.select { Users.login eq login }.limit(1).single().let { rowToTask(it) }
    }
  }

  private fun rowToTask(row: ResultRow): UserDTO = UserDTO(
    id = row[id],
    login = row[login],
    name = row[name],
    telegram_bot_token = row[telegram_bot_token],
    telegram_bot_chat_id = row[telegram_bot_chat_id],
    password = row[password],
    bio = row[bio],
  )

  suspend fun getUserIdFromJWT(call: ApplicationCall): String? {
    return try {
      Users.fetchById(call.principal<JWTPrincipal>()!!.payload.getClaim("id").asString()).id
    } catch (e: Exception) {
      call.respond(HttpStatusCode.Unauthorized, "User not found")
      null
    }
  }

  fun userToProfile(userDTO: UserDTO): UserProfileDTO = UserProfileDTO(
    login = userDTO.login,
    name = userDTO.name,
    telegram_bot_token = userDTO.telegram_bot_token,
    telegram_bot_chat_id = userDTO.telegram_bot_chat_id,
    bio = userDTO.bio,
  )

}