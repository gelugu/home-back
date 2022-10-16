package com.gelugu.home.database.users.dto

import kotlinx.serialization.Serializable

@Serializable
data class LoginPasswordDTO(
  val login: String,
  val password: String
)
