package com.gelugu.home.database.tasks

import com.gelugu.home.cache.InMemoryCache
import com.gelugu.home.database.tasks.dto.TaskDTO
import com.gelugu.home.database.tasks.dto.TaskUpdateDTO
import com.gelugu.home.database.users.Users
import com.gelugu.home.features.TelegramBot
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.javatime.timestamp
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*
import kotlin.concurrent.schedule

object Tasks : Table() {
  private val id = Tasks.varchar("id", 64)
  private val track_id = Tasks.varchar("track_id", 64)
  private val name = Tasks.varchar("name", 64)
  private val description = Tasks.text("description").default("")
  private val open = Tasks.bool("open").default(true)
  private val create_date = Tasks.timestamp("create_date").clientDefault { Date().toInstant() }
  private val parent_id = Tasks.varchar("parent_id", 64).nullable().default(null)
  private val due_date = Tasks.timestamp("due_date").nullable().default(null)
  private val schedule_date = Tasks.timestamp("schedule_date").nullable().default(null)

  fun create(userId: String, taskDTO: TaskDTO) {
    transaction {
      Tasks.insert { task ->
        task[id] = taskDTO.id
        task[track_id] = taskDTO.track_id
        task[name] = taskDTO.name
        task[create_date] = Date(taskDTO.create_date).toInstant()
        task[description] = taskDTO.description
        task[open] = taskDTO.open
        task[parent_id] = taskDTO.parent_id
        task[due_date] = taskDTO.due_date?.let { Date(it).toInstant() }
        task[schedule_date] = taskDTO.schedule_date?.let { Date(it).toInstant() }
      }

      val user = Users.fetchById(userId)
      if (user.telegram_bot_token.isNotEmpty() && user.telegram_bot_chat_id.isNotEmpty()) {
        taskDTO.schedule_date?.let {
          schedule(userId, taskDTO.id, it, user.telegram_bot_token, user.telegram_bot_chat_id)
        }
      }
    }
  }

  fun update(userId: String, taskId: String, taskDTO: TaskUpdateDTO) {
    return transaction {
      Tasks.update({ Tasks.id eq taskId }) { task ->
        taskDTO.name?.let { task[name] = it }
        taskDTO.track_id?.let { task[track_id] = it }
        taskDTO.description?.let { task[description] = it }
        taskDTO.open?.let { task[open] = it }
        taskDTO.parent_id?.let { task[parent_id] = it }
        taskDTO.due_date?.let {
          if (it == 0L) task[due_date] = null
          else task[due_date] = Date(it).toInstant()
        }
        taskDTO.schedule_date?.let {
          if (it == 0L) {
            task[schedule_date] = null
            InMemoryCache.timers[taskId]?.let {
              cancelTimer(taskId)
            }
          } else {
            task[schedule_date] = Date(it).toInstant()
            val user = Users.fetchById(userId)
            if (user.telegram_bot_token.isNotEmpty() && user.telegram_bot_chat_id.isNotEmpty()) {
              schedule(userId, taskId, it, user.telegram_bot_token, user.telegram_bot_chat_id)
            } else {
            }
          }
        }
      }
    }
  }

  fun delete(taskId: String) {
    transaction {
      Tasks.deleteWhere { Tasks.id eq taskId }
      cancelTimer(taskId)
    }
  }

  fun fetchTasks(trackId: String): List<TaskDTO> {
    return transaction {
      val tasksQuery = Tasks.select { track_id eq trackId }
      tasksQuery.sortedBy { create_date }
        .map { rowToTask(it) }
    }
  }

  fun fetchTask(taskId: String, id: String): TaskDTO {
    return transaction {
      Tasks.select { track_id eq taskId; Tasks.id eq id }.limit(1).single().let { rowToTask(it) }
    }
  }

  private fun rowToTask(row: ResultRow): TaskDTO = TaskDTO(
    id = row[id],
    track_id = row[track_id],
    name = row[name],
    create_date = row[create_date].toEpochMilli(),
    description = row[description],
    open = row[open],
    parent_id = row[parent_id],
    due_date = row[due_date]?.toEpochMilli(),
    schedule_date = row[schedule_date]?.toEpochMilli(),
  )

  private fun schedule(userId: String, taskId: String, timestamp: Long, telegramToken: String, chatId: String) {
    cancelTimer(taskId)

    val timerTask = Timer(taskId).schedule(Date(timestamp)) {
      val task = fetchTask(userId, taskId)

      TelegramBot(telegramToken, chatId).sendMessage(
        task.name +
            "\n\n" +
            task.description
      )

      cancelTimer(taskId)
    }

    InMemoryCache.timers[taskId] = timerTask
  }

  private fun cancelTimer(taskId: String) {
    InMemoryCache.timers[taskId]?.let {
      it.cancel()
      InMemoryCache.timers.remove(taskId)
    }
  }
}