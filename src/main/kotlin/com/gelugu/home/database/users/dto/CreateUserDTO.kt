package com.gelugu.home.database.users.dto

import kotlinx.serialization.Serializable

@Serializable
data class CreateUserDTO(
  val login: String,
  val name: String,
  val password: String,
)
