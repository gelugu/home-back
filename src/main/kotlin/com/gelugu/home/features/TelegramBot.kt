package com.gelugu.home.features

import com.gelugu.home.routing.registration.StatusRespondModel
import com.gelugu.home.routing.registration.TelegramUpdateRespond
import com.gelugu.home.routing.registration.TelegramUpdateRespondChat
import io.ktor.http.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString
import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.Collections.max

class TelegramBot(
  private val telegramBotToken: String,
  private val chatId: String = ""
) {
  fun getLastChatId(): TelegramUpdateRespondChat {
    val responseString = sendRequest("getUpdates").body()

    if (!responseString.contains("\"ok\":true")) {
      throw NullPointerException("Telegram token does not exist")
    }

    val response = Json.decodeFromString<TelegramUpdateRespond>(responseString)
    val latestMessageDate = max(response.result.map { it.message.date })
    val lastMessage = response.result.find { it.message.date == latestMessageDate }
      ?: throw NullPointerException("Telegram token does not exist")
    lastMessage.message.chat

    return lastMessage.message.chat
  }

  fun sendMessage(text: String): HttpStatusCode {
    return try {
      val response = sendRequest("sendMessage", mapOf("chat_id" to chatId, "text" to text))
      HttpStatusCode.fromValue(response.statusCode())
    } catch (e: java.lang.Exception) {
      e.printStackTrace()
      HttpStatusCode.InternalServerError
    }
  }

  private fun sendRequest(apiMethod: String, values: Map<String, String> = mapOf()): HttpResponse<String> {
    val client = HttpClient.newBuilder().build()

    val requestBuilder = HttpRequest.newBuilder()
      .uri(URI.create("https://api.telegram.org/bot$telegramBotToken/$apiMethod"))

    if (values.isNotEmpty()) {
      requestBuilder.POST(formData(values))
      requestBuilder.header("Content-Type", "application/x-www-form-urlencoded")
    }

    return client.send(requestBuilder.build(), HttpResponse.BodyHandlers.ofString())
  }

  private fun String.utf8(): String = URLEncoder.encode(this, "UTF-8")

  private fun formData(data: Map<String, String>): HttpRequest.BodyPublisher? {

    val res = data.map { (k, v) -> "${(k.utf8())}=${v.utf8()}" }
      .joinToString("&")

    return HttpRequest.BodyPublishers.ofString(res)
  }

}