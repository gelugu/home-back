package com.gelugu.home.database.tasks

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

object Tasks: Table() {
  private val id = Tasks.varchar("name", 64)
  private val name = Tasks.varchar("name", 64)
  private val description = Tasks.varchar("description", 512).nullable()
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
    val tasks = Tasks.selectAll().orderBy(name)
    return listOf()
  }
}