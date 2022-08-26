package com.gelugu.home.database.tasks

@kotlinx.serialization.Serializable
data class TaskCreateDTO(
  val name: String,
  val user_id: String,
  val description: String? = "",
  val parent_id: String? = null,
  val due_date: Long? = null,
  val schedule_date: Long? = null
)