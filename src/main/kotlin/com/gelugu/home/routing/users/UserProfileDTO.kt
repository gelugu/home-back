package com.gelugu.home.routing.users

import kotlinx.serialization.Serializable

@Serializable
class UserProfileDTO(
  val login: String,
  val name: String,
  val telegram_bot_token: String,
  val telegram_bot_chat_id: String,
  val bio: String,
)