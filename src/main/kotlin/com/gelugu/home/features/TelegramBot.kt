package com.gelugu.home.features

import com.gelugu.home.database.registration.RegistrationRespondModel
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
  fun getLastChatId(): RegistrationRespondModel {
    val response = sendRequest("getUpdates")

    val chatRegex = "chat\":\\{\"id\":\\d+".toRegex()
    val chatId = chatRegex.find(response.body())!!.value
      .replace("chat\":{\"id\":", "")

    val usernameRegex = "username\":\"[\\da-zA-Z]+".toRegex()
    val username = usernameRegex.find(response.body())!!.value
      .replace("username\":\"", "")

    return RegistrationRespondModel(username, chatId)
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