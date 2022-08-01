package com.gelugu.home.database.tasks

import kotlinx.serialization.Serializable

@Serializable
class TaskDTO(
  val id: String,
  val name: String,
  val description: String?,
  val open: Boolean?
)