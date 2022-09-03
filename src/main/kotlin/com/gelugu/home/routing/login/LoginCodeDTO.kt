package com.gelugu.home.routing.login

import kotlinx.serialization.Serializable

@Serializable
data class LoginCodeDTO(
  val login: String,
  val code: String
)
