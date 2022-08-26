package com.gelugu.home.routing.tasks

import com.gelugu.home.database.tasks.TaskCreateDTO
import com.gelugu.home.database.tasks.TaskDTO
import com.gelugu.home.database.tasks.TaskUpdateDTO
import com.gelugu.home.database.tasks.Tasks
import com.gelugu.home.database.users.LoginPasswordDTO
import com.gelugu.home.database.users.Users
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.Date
import java.util.UUID

fun Application.configureTasksRouting() {
  routing {

    authenticate("jwt") {
      post("/tasks/create") {
        val task = call.receive<TaskCreateDTO>()
        if (task.name.isNotEmpty()) {
          call.application.log.info(task.toString())
          Tasks.create(
            TaskDTO(
              id = UUID.randomUUID().toString(),
              create_date = Date().time,
              open = true,
              hidden = false,

              user_id = Users.fetchByLogin(call.receive<LoginPasswordDTO>().login).id,
              name = task.name,
              description = task.description ?: "",
              parent_id = task.parent_id,
              due_date = task.due_date,
              schedule_date = task.schedule_date
            )
          )
          call.respond(HttpStatusCode.Created, task)
        } else {
          call.respond(HttpStatusCode.BadRequest, "Can't create task with empty name")
        }
      }
      put("/tasks/{id}") {
        call.parameters["id"]?.let { id ->
          try {
            val task = call.receive<TaskUpdateDTO>()
            Tasks.update(id, task)
            call.respond(HttpStatusCode.OK, Tasks.fetchTask(id))
          } catch (e: NoSuchElementException) {
            call.respond(HttpStatusCode.NotFound, "Task with id $id not found")
          }
        } ?: run {
          call.respond(HttpStatusCode.BadRequest, "Can't update task with id")
        }
      }
      delete("/tasks/{id}") {
        call.parameters["id"]?.let { id ->
          try {
            val task = Tasks.fetchTask(id)
            Tasks.delete(id)
            call.respond(HttpStatusCode.OK, task)
          } catch (e: NoSuchElementException) {
            call.respond(HttpStatusCode.NotFound, "Task with id $id not found")
          }
        } ?: run {
          call.respond(HttpStatusCode.BadRequest, "Can't delete task with id")
        }
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
        val hidden = call.request.queryParameters["hidden"] == "true"
        call.respond(HttpStatusCode.OK, Tasks.fetchTasks(hidden))
      }
    }
  }
}