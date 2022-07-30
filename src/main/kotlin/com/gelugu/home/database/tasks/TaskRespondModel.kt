package com.gelugu.home.database.tasks

@kotlinx.serialization.Serializable
data class TaskRespondModel(
  val id: String,
  val name: String,
  val description: String,
  val open: Boolean
)
