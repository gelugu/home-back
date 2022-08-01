package com.gelugu.home.routing.login

import kotlinx.serialization.Serializable

@Serializable
data class LoginRespondModel(
  val token: String
)
