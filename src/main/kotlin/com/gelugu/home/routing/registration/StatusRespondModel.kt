package com.gelugu.home.routing.registration

import kotlinx.serialization.Serializable

@Serializable
data class StatusRespondModel(
  val username: String,
  val chat: String
)