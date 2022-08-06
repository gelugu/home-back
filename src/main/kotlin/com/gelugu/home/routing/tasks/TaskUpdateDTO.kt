package com.gelugu.home.routing.tasks

@kotlinx.serialization.Serializable
data class TaskUpdateDTO(
  val name: String? = null,
  val description: String? = null,
  val open: Boolean? = null,
  val parent_id: String? = null,
  val due_date: Long? = null,
  val schedule_date: Long? = null,
  val hidden: Boolean? = null
)
