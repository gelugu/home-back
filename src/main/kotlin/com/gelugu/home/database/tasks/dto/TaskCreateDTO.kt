package com.gelugu.home.database.tasks.dto

@kotlinx.serialization.Serializable
data class TaskCreateDTO(
  val name: String,
  val track_id: String,
  val description: String? = "",
  val parent_id: String? = null,
  val due_date: Long? = null,
  val schedule_date: Long? = null
)
