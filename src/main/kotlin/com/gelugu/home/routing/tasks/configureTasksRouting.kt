package com.gelugu.home.routing.tasks

import com.gelugu.home.database.tasks.dto.TaskCreateDTO
import com.gelugu.home.database.tasks.dto.TaskDTO
import com.gelugu.home.database.tasks.dto.TaskUpdateDTO
import com.gelugu.home.database.tasks.Tasks
import com.gelugu.home.database.tracks.Tracks
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

    val rootRoute = "/tasks"
    val idRoute = "$rootRoute/{id}"

    authenticate("jwt") {

      get(rootRoute) {
        val trackId = call.request.queryParameters["track"] ?: "default"
        call.respond(HttpStatusCode.OK, Tasks.fetchTasks(trackId))
      }

      get(idRoute) {
        Users.getUserIdFromJWT(call)?.let { userId ->
          val id = call.parameters["id"]
          id?.let { taskId ->
            try {
              call.respond(HttpStatusCode.OK, Tasks.fetchTask(userId, taskId))
            } catch (e: NoSuchElementException) {
              call.respond(HttpStatusCode.NotFound, "Task with id $taskId not found")
            }
          } ?: run {
            call.respond(HttpStatusCode.NotFound, "Task id is null")
          }
        }
      }

      post(rootRoute) {
        Users.getUserIdFromJWT(call)?.let { userId ->
          val task = call.receive<TaskCreateDTO>()
          if (task.name.isNotEmpty()) {
            call.application.log.info(task.toString())
            val taskId = UUID.randomUUID().toString()
            Tasks.create(
              userId,
              TaskDTO(
                id = taskId,
                create_date = Date().time,
                open = true,

                track_id = task.track_id,
                name = task.name,
                description = task.description ?: "",
                parent_id = task.parent_id,
                due_date = task.due_date,
                schedule_date = task.schedule_date
              )
            )
            call.respond(HttpStatusCode.Created, Tasks.fetchTask(userId, taskId))
          } else {
            call.respond(HttpStatusCode.BadRequest, "Can't create task with empty name")
          }
        }
      }

      put(idRoute) {
        Users.getUserIdFromJWT(call)?.let { userId ->
          call.parameters["id"]?.let { id ->
            try {
              val task = call.receive<TaskUpdateDTO>()
              Tasks.update(userId, id, task)
              call.respond(HttpStatusCode.OK, Tasks.fetchTask(userId, id))
            } catch (e: NoSuchElementException) {
              call.respond(HttpStatusCode.NotFound, "Task with id $id not found")
            }
          } ?: run {
            call.respond(HttpStatusCode.BadRequest, "Can't update task with id")
          }
        }
      }

      delete(idRoute) {
        Users.getUserIdFromJWT(call)?.let { userId ->
          call.parameters["id"]?.let { id ->
            try {
              val task = Tasks.fetchTask(userId, id)
              Tasks.delete(id)
              call.respond(HttpStatusCode.OK, task)
            } catch (e: NoSuchElementException) {
              call.respond(HttpStatusCode.NotFound, "Task with id $id not found")
            }
          } ?: run {
            call.respond(HttpStatusCode.BadRequest, "Can't delete task with id")
          }
        }
      }
    }
  }
}