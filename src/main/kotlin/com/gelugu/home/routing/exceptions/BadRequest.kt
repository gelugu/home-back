package com.gelugu.home.routing.exceptions

import io.ktor.http.*
import kotlinx.serialization.Serializable

@Serializable
data class BadRequest(
  val message: String,
  val status: Int = HttpStatusCode.BadRequest.value
)
