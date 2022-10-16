package com.gelugu.home.database.users.dto

import kotlinx.serialization.Serializable

@Serializable
class UpdateUserDTO(
  val login: String? = null,
  val name: String? = null,
  val telegram_bot_token: String? = null,
  val telegram_bot_chat_id: String? = null,
  val password: String? = null,
  val bio: String? = null,
)