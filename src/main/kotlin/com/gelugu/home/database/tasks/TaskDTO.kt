package com.gelugu.home.database.tasks

import kotlinx.serialization.Serializable

@Serializable
class TaskDTO(
  val id: String,
  val user_id: String,
  val name: String,
  val create_date: Long,
  val description: String,
  val open: Boolean,
  val parent_id: String?,
  val due_date: Long?,
  val schedule_date: Long?,
  val hidden: Boolean
)