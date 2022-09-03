package com.gelugu.home.routing.login

import kotlinx.serialization.Serializable

@Serializable
data class LoginPasswordDTO(
  val login: String,
  val password: String
)
