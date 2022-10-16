package com.gelugu.home.database.users.dto

import kotlinx.serialization.Serializable

@Serializable
class UserProfileDTO(
  val id: String,
  val login: String,
  val name: String,
  val telegram_bot_token: String,
  val telegram_bot_chat_id: String,
  val bio: String,
)