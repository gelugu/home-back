package com.gelugu.home.database.tasks

import com.gelugu.home.cache.InMemoryCache
import com.gelugu.home.database.users.Users
import com.gelugu.home.features.TelegramBot
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.javatime.timestamp
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*
import kotlin.concurrent.schedule

object Tasks : Table() {
  private val id = Tasks.varchar("id", 64)
  private val user_id = Tasks.varchar("user_id", 64)
  private val name = Tasks.varchar("name", 64)
  private val description = Tasks.text("description").default("")
  private val open = Tasks.bool("open").default(true)
  private val create_date = Tasks.timestamp("create_date").clientDefault { Date().toInstant() }
  private val parent_id = Tasks.varchar("parent_id", 64).nullable().default(null)
  private val due_date = Tasks.timestamp("due_date").nullable().default(null)
  private val schedule_date = Tasks.timestamp("schedule_date").nullable().default(null)
  private val hidden = Tasks.bool("hidden").default(false)

  fun create(taskDTO: TaskDTO) {
    transaction {
      Tasks.insert { task ->
        task[id] = taskDTO.id
        task[user_id] = taskDTO.user_id
        task[name] = taskDTO.name
        task[create_date] = Date(taskDTO.create_date).toInstant()
        task[description] = taskDTO.description
        task[open] = taskDTO.open
        task[parent_id] = taskDTO.parent_id
        task[due_date] = taskDTO.due_date?.let { Date(it).toInstant() }
        task[schedule_date] = taskDTO.schedule_date?.let { Date(it).toInstant() }
        task[hidden] = taskDTO.hidden
      }

      val user = Users.fetchById(taskDTO.user_id)
      if (user.telegram_bot_token.isNotEmpty() && user.telegram_bot_chat_id.isNotEmpty()) {
        taskDTO.schedule_date?.let {
          schedule(taskDTO.id, it, user.telegram_bot_token, user.telegram_bot_chat_id)
        }
      }
    }
  }

  fun update(taskId: String, taskDTO: TaskUpdateDTO) {
    return transaction {
      Tasks.update({ Tasks.id eq taskId }) { task ->
        taskDTO.name?.let { task[name] = it }
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
          }
          else {
            task[schedule_date] = Date(it).toInstant()
            val user = Users.fetchById(fetchTask(taskId).user_id)
            if (user.telegram_bot_token.isNotEmpty() && user.telegram_bot_chat_id.isNotEmpty()) {
              schedule(taskId, it, user.telegram_bot_token, user.telegram_bot_chat_id)
            } else {}
          }
        }
        taskDTO.hidden?.let { task[hidden] = it }
      }
    }
  }

  fun delete(taskId: String) {
    transaction {
      Tasks.deleteWhere { Tasks.id eq taskId }
      cancelTimer(taskId)
    }
  }

  fun fetchTasks(showHidden: Boolean = false): List<TaskDTO> {
    return transaction {
      val tasksQuery = if (showHidden) Tasks.selectAll() else Tasks.select { hidden eq false }
      tasksQuery.sortedBy { create_date }
        .map { rowToTask(it) }
    }
  }

  fun fetchTask(id: String): TaskDTO {
    return transaction {
      Tasks.select { Tasks.id eq id }.limit(1).single().let { rowToTask(it) }
    }
  }

  private fun rowToTask(row: ResultRow): TaskDTO = TaskDTO(
    id = row[id],
    user_id = row[user_id],
    name = row[name],
    create_date = row[create_date].toEpochMilli(),
    description = row[description],
    open = row[open],
    parent_id = row[parent_id],
    due_date = row[due_date]?.toEpochMilli(),
    schedule_date = row[schedule_date]?.toEpochMilli(),
    hidden = row[hidden]
  )

  private fun schedule(taskId: String, timestamp: Long, telegramToken: String, chatId: String) {
    cancelTimer(taskId)

    val timerTask = Timer(taskId).schedule(Date(timestamp)) {
      val task = fetchTask(taskId)

      TelegramBot(telegramToken, chatId).sendMessage("""
        ${task.name}
        
        ${task.description}
      """.trimIndent())

      cancelTimer(taskId)
    }

    InMemoryCache.timers[taskId] = timerTask
  }

  private fun cancelTimer(taskId: String) {
    InMemoryCache.timers[taskId]?.let {
      it.cancel()
      InMemoryCache.timers.remove(taskId)
    } ?: throw Exception("Canceled timer didn't found for task \"${taskId}\"")
  }
}