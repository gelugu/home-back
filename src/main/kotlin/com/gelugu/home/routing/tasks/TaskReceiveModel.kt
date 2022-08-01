package com.gelugu.home.routing.tasks

@kotlinx.serialization.Serializable
data class TaskReceiveModel(
  val name: String,
  val description: String? = ""
)
