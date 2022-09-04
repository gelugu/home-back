package com.gelugu.home.tasks

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.gelugu.home.configurations.ApplicationConfig
import com.gelugu.home.database.tasks.TaskCreateDTO
import com.gelugu.home.database.tasks.TaskDTO
import com.gelugu.home.database.tasks.TaskUpdateDTO
import com.gelugu.home.database.tasks.Tasks
import com.gelugu.home.plugins.configureSerialization
import com.gelugu.home.plugins.connectDatabase
import com.gelugu.home.plugins.installJWT
import com.gelugu.home.routing.tasks.configureTasksRouting
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.serialization.encodeToString
import org.junit.Test
import kotlin.test.assertEquals
import kotlinx.serialization.json.Json
import java.util.*

open class TasksTest {
  private val userId = "098go86fg"
  private val token =  JWT.create()
    .withClaim("id", userId)
    .withExpiresAt(Date(System.currentTimeMillis() + ApplicationConfig.tokenExpirationTime))
    .sign(Algorithm.HMAC256(ApplicationConfig.jwtSecret))
  private val wrongToken =  JWT.create()
    .withClaim("id", "i-am-not-exist")
    .withExpiresAt(Date(System.currentTimeMillis() + ApplicationConfig.tokenExpirationTime))
    .sign(Algorithm.HMAC256(ApplicationConfig.jwtSecret))

  @Test
  fun `Get all tasks for existing user`() = testApplication {
    connectDatabase()
    application {
      configureSerialization()
      installJWT()
      configureTasksRouting()
    }
    val tasks = Tasks.fetchTasks(userId)
    client.get("/tasks") {
      accept(ContentType.Application.Json)
      bearerAuth(token)
    }.apply {
      assertEquals(HttpStatusCode.OK, status)
      assertEquals(Json.encodeToString(tasks), bodyAsText())
    }
  }

  @Test
  fun `Get all tasks for not existing user`() = testApplication {
    connectDatabase()
    application {
      configureSerialization()
      installJWT()
      configureTasksRouting()
    }
    client.get("/tasks") {
      accept(ContentType.Application.Json)
      bearerAuth(wrongToken)
    }.apply {
      assertEquals(HttpStatusCode.Unauthorized, status)
      assertEquals(Json.encodeToString("User not found"), bodyAsText())
    }
  }

  @Test
  fun `Get all tasks without authorization token`() = testApplication {
    connectDatabase()
    application {
      configureSerialization()
      installJWT()
      configureTasksRouting()
    }
    client.get("/tasks") {
      accept(ContentType.Application.Json)
    }.apply {
      assertEquals(HttpStatusCode.Unauthorized, status)
      assertEquals(Json.encodeToString("Token is not exist, not valid or has expired"), bodyAsText())
    }
  }

  @Test
  fun `Get one task for existing user`() = testApplication {
    connectDatabase()
    application {
      configureSerialization()
      installJWT()
      configureTasksRouting()
    }
    val task = Tasks.fetchTasks(userId)[0]
    client.get("/tasks/${task.id}") {
      accept(ContentType.Application.Json)
      bearerAuth(token)
    }.apply {
      assertEquals(HttpStatusCode.OK, status)
      assertEquals(Json.encodeToString(task), bodyAsText())
    }
  }

  @Test
  fun `Get one task for not existing user`() = testApplication {
    connectDatabase()
    application {
      configureSerialization()
      installJWT()
      configureTasksRouting()
    }
    val task = Tasks.fetchTasks(userId)[0]
    client.get("/tasks/${task.id}") {
      accept(ContentType.Application.Json)
      bearerAuth(wrongToken)
    }.apply {
      assertEquals(HttpStatusCode.Unauthorized, status)
      assertEquals(Json.encodeToString("User not found"), bodyAsText())
    }
  }

  @Test
  fun `Get one task without authorization token`() = testApplication {
    connectDatabase()
    application {
      configureSerialization()
      installJWT()
      configureTasksRouting()
    }
    client.get("/tasks/whatever") {
      accept(ContentType.Application.Json)
    }.apply {
      assertEquals(HttpStatusCode.Unauthorized, status)
      assertEquals(Json.encodeToString("Token is not exist, not valid or has expired"), bodyAsText())
    }
  }

  @Test
  fun `Get not existing task`() = testApplication {
    connectDatabase()
    application {
      configureSerialization()
      installJWT()
      configureTasksRouting()
    }
    val wrongId = "i-am-not-exist"
    client.get("/tasks/$wrongId") {
      accept(ContentType.Application.Json)
      bearerAuth(token)
    }.apply {
      assertEquals(HttpStatusCode.NotFound, status)
      assertEquals(Json.encodeToString("Task with id $wrongId not found"), bodyAsText())
    }
  }

  @Test
  fun `Create task for existing user`() = testApplication {
    connectDatabase()
    application {
      configureSerialization()
      installJWT()
      configureTasksRouting()
    }
    val task = TaskCreateDTO(
      name = "Test task",
    )
    client.post("/tasks") {
      contentType(ContentType.Application.Json)
      accept(ContentType.Application.Json)
      bearerAuth(token)
      setBody(Json.encodeToString(task))
    }.apply {
      val responseTaskId = "\"id\":\"([a-z\\d-]+)\"".toRegex().find(bodyAsText())!!.groupValues[1]
      assertEquals(HttpStatusCode.Created, status)
      assertEquals(Json.encodeToString(Tasks.fetchTask(userId, responseTaskId)), bodyAsText())
    }
  }

  @Test
  fun `Create task for not existing user`() = testApplication {
    connectDatabase()
    application {
      configureSerialization()
      installJWT()
      configureTasksRouting()
    }
    val task = TaskCreateDTO(
      name = "Test task",
    )
    client.post("/tasks") {
      contentType(ContentType.Application.Json)
      accept(ContentType.Application.Json)
      bearerAuth(wrongToken)
      setBody(Json.encodeToString(task))
    }.apply {
      assertEquals(HttpStatusCode.Unauthorized, status)
      assertEquals(Json.encodeToString("User not found"), bodyAsText())
    }
  }

  @Test
  fun `Create task without authorization token`() = testApplication {
    connectDatabase()
    application {
      configureSerialization()
      installJWT()
      configureTasksRouting()
    }
    val task = TaskCreateDTO(
      name = "Test task",
    )
    client.post("/tasks") {
      contentType(ContentType.Application.Json)
      accept(ContentType.Application.Json)
      setBody(Json.encodeToString(task))
    }.apply {
      assertEquals(HttpStatusCode.Unauthorized, status)
      assertEquals(Json.encodeToString("Token is not exist, not valid or has expired"), bodyAsText())
    }
  }

  @Test
  fun `Update task for existing user`() = testApplication {
    connectDatabase()
    application {
      configureSerialization()
      installJWT()
      configureTasksRouting()
    }
    val task = Tasks.fetchTasks(userId)[0]
    val newTask = TaskDTO(
      id = task.id,
      user_id = task.user_id,
      name = task.name + "new",
      create_date = task.create_date,
      description = task.description + "new",
      open = !task.open,
      parent_id = task.parent_id,
      due_date = task.due_date,
      schedule_date = task.schedule_date,
    )
    val updateTask = TaskUpdateDTO(
      name = newTask.name,
      description = newTask.description,
      open = newTask.open,
      parent_id = newTask.parent_id,
      due_date = newTask.due_date,
      schedule_date = newTask.schedule_date,
    )
    client.put("/tasks/${task.id}") {
      contentType(ContentType.Application.Json)
      accept(ContentType.Application.Json)
      bearerAuth(token)
      setBody(Json.encodeToString(updateTask))
    }.apply {
      assertEquals(HttpStatusCode.OK, status)
      val dbTask = Tasks.fetchTask(userId, task.id)
      assertEquals(Json.encodeToString(newTask), Json.encodeToString(dbTask))
      assertEquals(Json.encodeToString(dbTask), bodyAsText())
    }
  }

  @Test
  fun `Update task for not existing user`() = testApplication {
    connectDatabase()
    application {
      configureSerialization()
      installJWT()
      configureTasksRouting()
    }
    val task = Tasks.fetchTasks(userId)[0]
    val updateTask = TaskUpdateDTO(
      name = "whatever",
    )
    client.put("/tasks/${task.id}") {
      contentType(ContentType.Application.Json)
      accept(ContentType.Application.Json)
      bearerAuth(wrongToken)
      setBody(Json.encodeToString(updateTask))
    }.apply {
      assertEquals(HttpStatusCode.Unauthorized, status)
      assertEquals(Json.encodeToString("User not found"), bodyAsText())
    }
  }

  @Test
  fun `Update task without authorization token`() = testApplication {
    connectDatabase()
    application {
      configureSerialization()
      installJWT()
      configureTasksRouting()
    }
    val task = Tasks.fetchTasks(userId)[0]
    val updateTask = TaskUpdateDTO(
      name = "whatever",
    )
    client.put("/tasks/${task.id}") {
      contentType(ContentType.Application.Json)
      accept(ContentType.Application.Json)
      setBody(Json.encodeToString(updateTask))
    }.apply {
      assertEquals(HttpStatusCode.Unauthorized, status)
      assertEquals(Json.encodeToString("Token is not exist, not valid or has expired"), bodyAsText())
    }
  }

  @Test
  fun `Update not existing task`() = testApplication {
    connectDatabase()
    application {
      configureSerialization()
      installJWT()
      configureTasksRouting()
    }
    val taskId = "i-am-not-exist"
    client.put("/tasks/$taskId") {
      contentType(ContentType.Application.Json)
      accept(ContentType.Application.Json)
      bearerAuth(token)
      setBody(Json.encodeToString(TaskUpdateDTO("whatever")))
    }.apply {
      assertEquals(HttpStatusCode.NotFound, status)
      assertEquals(Json.encodeToString("Task with id $taskId not found"), bodyAsText())
    }
  }

  @Test
  fun `Delete task for existing user`() = testApplication {
    connectDatabase()
    application {
      configureSerialization()
      installJWT()
      configureTasksRouting()
    }
    val task = Tasks.fetchTasks(userId)[0]
    client.delete("/tasks/${task.id}") {
      accept(ContentType.Application.Json)
      bearerAuth(token)
    }.apply {
      assertEquals(HttpStatusCode.OK, status)
      assertEquals(Json.encodeToString(task), bodyAsText())
    }
  }

  @Test
  fun `Delete task for not existing user`() = testApplication {
    connectDatabase()
    application {
      configureSerialization()
      installJWT()
      configureTasksRouting()
    }
    val task = Tasks.fetchTasks(userId)[0]
    client.delete("/tasks/${task.id}") {
      accept(ContentType.Application.Json)
      bearerAuth(wrongToken)
    }.apply {
      assertEquals(HttpStatusCode.Unauthorized, status)
      assertEquals(Json.encodeToString("User not found"), bodyAsText())
    }
  }

  @Test
  fun `Delete task without authorization token`() = testApplication {
    connectDatabase()
    application {
      configureSerialization()
      installJWT()
      configureTasksRouting()
    }
    val task = Tasks.fetchTasks(userId)[0]
    client.delete("/tasks/${task.id}") {
      accept(ContentType.Application.Json)
    }.apply {
      assertEquals(HttpStatusCode.Unauthorized, status)
      assertEquals(Json.encodeToString("Token is not exist, not valid or has expired"), bodyAsText())
    }
  }

  @Test
  fun `Delete not existing task`() = testApplication {
    connectDatabase()
    application {
      configureSerialization()
      installJWT()
      configureTasksRouting()
    }
    val task = Tasks.fetchTasks(userId)[0]
    client.delete("/tasks/${task.id}") {
      accept(ContentType.Application.Json)
      bearerAuth(token)
    }.apply {
      assertEquals(HttpStatusCode.OK, status)
    }
  }
}