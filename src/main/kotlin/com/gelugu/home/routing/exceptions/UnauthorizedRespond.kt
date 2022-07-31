package com.gelugu.home.routing.exceptions

import io.ktor.http.*
import kotlinx.serialization.Serializable

@Serializable
data class UnauthorizedRespond(
  val message: String,
  val status: Int = HttpStatusCode.Unauthorized.value
)
