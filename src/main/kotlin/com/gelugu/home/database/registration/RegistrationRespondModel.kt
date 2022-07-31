package com.gelugu.home.database.registration

import kotlinx.serialization.Serializable

@Serializable
data class RegistrationRespondModel(
  val username: String,
  val chat: String
)
