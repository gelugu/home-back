package com.gelugu.home.database.tracks.dto

@kotlinx.serialization.Serializable
data class TrackCreateDTO(
  val name: String,
  val description: String
)
