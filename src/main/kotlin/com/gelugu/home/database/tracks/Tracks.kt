package com.gelugu.home.database.tracks

import com.gelugu.home.database.tasks.Tasks
import com.gelugu.home.database.tracks.dto.TrackDTO
import com.gelugu.home.database.tracks.dto.TrackUpdateDTO
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

object Tracks : Table() {
  private val id = Tracks.varchar("id", 64)
  private val name = Tracks.varchar("name", 64)
  private val description = Tracks.text("description")
  private val owner = Tracks.varchar("owner", 64)

  fun create(userId: String, trackDTO: TrackDTO) {
    transaction {
      Tracks.insert { track ->
        track[id] = trackDTO.id
        track[name] = trackDTO.name
        track[description] = trackDTO.description
        track[owner] = userId
      }
    }
  }

  fun update(trackId: String, trackDTO: TrackUpdateDTO, userId: String) {
    return transaction {
      Tracks.update({
        Tracks.id eq trackId
        owner eq userId
      }) { track ->
        trackDTO.name?.let { track[name] = it }
        trackDTO.description?.let { track[description] = it }
      }
    }
  }

  fun delete(trackId: String) {
    transaction {
      Tasks.fetchTasks(trackId).map { it.id }.forEach {
        Tasks.delete(it)
      }
      Tracks.deleteWhere { Tracks.id eq trackId }
    }
  }

  fun fetchAll(userId: String): List<TrackDTO> {
    return transaction {
      val tracksQuery = Tracks.select { owner eq userId }
      tracksQuery.map { rowToTrack(it) }
    }
  }

  fun fetchOne(userId: String, id: String): TrackDTO {
    return transaction {
      Tracks.select { owner eq userId; Tracks.id eq id }.limit(1).single().let { rowToTrack(it) }
    }
  }

  private fun rowToTrack(row: ResultRow): TrackDTO = TrackDTO(
    id = row[id],
    name = row[name],
    description = row[description],
    owner = row[owner],
  )
}