package com.gelugu.home.routing.tasks

@kotlinx.serialization.Serializable
data class TaskCreateDTO(
  val name: String,
  val description: String? = "",
  val parent_id: String? = null,
  val due_date: Long? = null,
  val schedule_date: Long? = null
)
