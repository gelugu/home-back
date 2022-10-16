package com.gelugu.home.database.tracks.dto

@kotlinx.serialization.Serializable
data class TrackUpdateDTO(
  val id: String? = null,
  val name: String? = null,
  val description: String? = null
)
