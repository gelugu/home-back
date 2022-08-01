package com.gelugu.home.routing.tasks

import com.gelugu.home.database.tasks.TaskDTO
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
      Tasks.insert(
        TaskDTO(
          id = UUID.randomUUID().toString(),
          name = task.name,
          description = task.description,
          open = true
        )
      )
      call.respond(HttpStatusCode.Created, task)
    }
    get("/tasks/{id}") {
      val id = call.parameters["id"]
      id?.let {
        try {
          call.respond(HttpStatusCode.OK, Tasks.fetchTask(id))
        } catch (e: NoSuchElementException) {
          call.respond(HttpStatusCode.NotFound, "Task with id $id not found")
        }
      } ?: run {
        call.respond(HttpStatusCode.NotFound, "Task with id is Null")
      }
    }
    get("/tasks") {
      call.respond(HttpStatusCode.OK, Tasks.fetchTasks())
    }
  }
}