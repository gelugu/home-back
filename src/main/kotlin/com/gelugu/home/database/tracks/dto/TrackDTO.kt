package com.gelugu.home.database.tracks.dto

import kotlinx.serialization.Serializable

@Serializable
class TrackDTO(
  val id: String,
  val name: String,
  val description: String,
  val owner: String,
)