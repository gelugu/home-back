package com.gelugu.home.database.login

import kotlinx.serialization.Serializable

@Serializable
data class LoginRespondModel(
  val token: String
)
