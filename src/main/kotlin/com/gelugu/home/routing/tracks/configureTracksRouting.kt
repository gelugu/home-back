package com.gelugu.home.routing.tracks

import com.gelugu.home.database.tracks.Tracks
import com.gelugu.home.database.tracks.dto.TrackCreateDTO
import com.gelugu.home.database.tracks.dto.TrackDTO
import com.gelugu.home.database.tracks.dto.TrackUpdateDTO
import com.gelugu.home.database.users.Users
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.UUID

fun Application.configureTracksRouting() {
  routing {

    val rootRoute = "/tracks"
    val idRoute = "$rootRoute/{id}"

    authenticate("jwt") {

      get(rootRoute) {
        Users.getUserIdFromJWT(call)?.let { userId ->
          call.respond(HttpStatusCode.OK, Tracks.fetchAll(userId))
        }
      }

      get(idRoute) {
        Users.getUserIdFromJWT(call)?.let { userId ->
          val id = call.parameters["id"]
          id?.let { trackId ->
            try {
              call.respond(HttpStatusCode.OK, Tracks.fetchOne(userId, trackId))
            } catch (e: NoSuchElementException) {
              call.respond(HttpStatusCode.NotFound, "Track with id $trackId not found")
            }
          } ?: run {
            call.respond(HttpStatusCode.NotFound, "Track id is null")
          }
        }
      }

      post(rootRoute) {
        Users.getUserIdFromJWT(call)?.let { userId ->
          val track = call.receive<TrackCreateDTO>()
          if (track.name.isNotEmpty()) {
            call.application.log.info(track.toString())
            val trackId = UUID.randomUUID().toString()
            Tracks.create(
              userId,
              TrackDTO(
                id = trackId,
                owner = userId,
                name = track.name,
                description = track.description,
              )
            )
            call.respond(HttpStatusCode.Created, Tracks.fetchOne(userId, trackId))
          } else {
            call.respond(HttpStatusCode.BadRequest, "Can't create track with empty name")
          }
        }
      }

      put(idRoute) {
        Users.getUserIdFromJWT(call)?.let { userId ->
          call.parameters["id"]?.let { id ->
            try {
              val task = call.receive<TrackUpdateDTO>()
              Tracks.update(id, task, userId)
              call.respond(HttpStatusCode.OK, Tracks.fetchOne(userId, id))
            } catch (e: NoSuchElementException) {
              call.respond(HttpStatusCode.NotFound, "Track with id $id not found")
            }
          } ?: run {
            call.respond(HttpStatusCode.BadRequest, "Can't update track with id")
          }
        }
      }

      delete(idRoute) {
        Users.getUserIdFromJWT(call)?.let { userId ->
          call.parameters["id"]?.let { id ->
            try {
              val track = Tracks.fetchOne(userId, id)
              Tracks.delete(id)
              call.respond(HttpStatusCode.OK, track)
            } catch (e: NoSuchElementException) {
              call.respond(HttpStatusCode.NotFound, "Track with id $id not found")
            }
          } ?: run {
            call.respond(HttpStatusCode.BadRequest, "Can't delete track with id")
          }
        }
      }
    }
  }
}