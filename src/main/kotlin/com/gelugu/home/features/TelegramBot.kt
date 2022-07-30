package com.gelugu.home.features

import io.ktor.http.*
import java.net.ConnectException
import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

class TelegramBot(
  private val telegramBotToken: String,
  private val chatId: String = ""
) {
  fun getLastChatId(): String {
    val response = sendRequest("getUpdates")
    println("CHAT ID RESPONSE")
    println(response.body())
    return response.body()
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

  private fun sendRequest(apiMethod: String, values: Map<String, String> = mapOf()):  HttpResponse<String> {
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

    val res = data.map {(k, v) -> "${(k.utf8())}=${v.utf8()}"}
      .joinToString("&")

    return HttpRequest.BodyPublishers.ofString(res)
  }

}