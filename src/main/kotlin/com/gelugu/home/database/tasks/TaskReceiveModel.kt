package com.gelugu.home.database.tasks

@kotlinx.serialization.Serializable
data class TaskReceiveModel(
  val name: String,
  val description: String = ""
)
