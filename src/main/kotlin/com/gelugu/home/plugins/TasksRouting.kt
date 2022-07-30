package com.gelugu.home.plugins

import com.gelugu.home.database.tasks.TaskDTO
import com.gelugu.home.database.tasks.TaskReceiveModel
import com.gelugu.home.database.tasks.TaskRespondModel
import com.gelugu.home.database.tasks.Tasks
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.UUID

fun Application.configureTasksRouting() {
  routing {
    post("/tasks/create") {
      val task = call.receive<TaskReceiveModel>()
      Tasks.insert(TaskDTO(
        id = UUID.randomUUID().toString(),
        name = task.name,
        description = task.description,
        open = true
      ))
      call.respond(HttpStatusCode.Created, task)
    }
    get("/tasks/id") {
      val task = TaskRespondModel(id = UUID.randomUUID().toString(), name = "task 1", description = "do and may rest", open = true)
      call.respond(HttpStatusCode.OK, task)
    }
    get("/tasks") {
      val task1 = TaskRespondModel(id = UUID.randomUUID().toString(), name = "task 1", description = "do and may rest", open = true)
      val task2 = TaskRespondModel(id = UUID.randomUUID().toString(), name = "task 2", description = "rest whatever u want", open = false)
      call.respond(HttpStatusCode.OK, listOf(task1, task2))
    }
  }
}