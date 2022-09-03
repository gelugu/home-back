package com.gelugu.home.routing.registration

import kotlinx.serialization.Serializable

@Serializable()
data class TelegramUpdateRespond(
  val ok: Boolean,
  val result: List<TelegramUpdateRespondResult>? = null,
  val error_code: Int? = null,
  val description: String? = null
)

@Serializable
data class TelegramUpdateRespondResult(
  val update_id: Int,
  val message: TelegramUpdateRespondMessage
)

@Serializable
data class TelegramUpdateRespondMessage(
  val message_id: Int,
  val from: TelegramUpdateRespondFrom,
  val chat: TelegramUpdateRespondChat,
  val date: Long,
  val text: String,
  val entities: List<TelegramUpdateRespondMessageEntity>? = null
)

@Serializable
data class TelegramUpdateRespondFrom(
  val id: Int,
  val is_bot: Boolean,
  val first_name: String,
  val last_name: String,
  val username: String,
  val language_code: String
)

@Serializable
data class TelegramUpdateRespondChat(
  val id: Int,
  val first_name: String,
  val last_name: String,
  val username: String,
  val type: String
)

@Serializable
data class TelegramUpdateRespondMessageEntity(
  val offset: Int,
  val length: Int,
  val type: String
)
