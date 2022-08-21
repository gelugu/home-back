package com.gelugu.home.database.users

import kotlinx.serialization.Serializable

@Serializable
data class LoginTelegramDTO(
  val login: String,
  val telegram_code: String,
)
