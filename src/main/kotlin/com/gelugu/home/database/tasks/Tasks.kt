package com.gelugu.home.database.tasks

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

object Tasks : Table() {
  private val id = Tasks.varchar("id", 64)
  private val name = Tasks.varchar("name", 64)
  private val description = Tasks.text("description").nullable()
  private val open = Tasks.bool("open").nullable()

  fun insert(taskDTO: TaskDTO) {
    transaction {
      Tasks.insert {
        it[id] = taskDTO.id
        it[name] = taskDTO.name
        it[description] = taskDTO.description
        it[open] = taskDTO.open
      }
    }
  }

  fun fetchTasks(): List<TaskDTO> {
    return transaction {
      Tasks.selectAll()
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
    name = row[name],
    description = row[description],
    open = row[open]
  )
}