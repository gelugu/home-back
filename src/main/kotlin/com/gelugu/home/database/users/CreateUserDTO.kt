package com.gelugu.home.database.users

import kotlinx.serialization.Serializable

@Serializable
data class CreateUserDTO(
  val login: String,
  val name: String,
  val telegram_bot_token: String,
  val telegram_bot_chat_id: String,
  val password: String,
)
