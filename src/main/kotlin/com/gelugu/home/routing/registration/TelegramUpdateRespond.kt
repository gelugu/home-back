package com.gelugu.home.routing.registration

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable()
data class TelegramUpdateRespond(
  val ok: Boolean,
  val result: List<TelegramUpdateRespondResult>
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

//{
//  "ok":true,
//  "result": [
//    {
//      "update_id":119782897,
//      "message": {
//        "message_id":448,
//        "from": {
//          "id":218596157,
//          "is_bot":false,
//          "first_name":"Mikhail",
//          "last_name":"Kraev",
//          "username":"gelugu",
//          "language_code":"en"
//        },
//        "chat": {
//          "id":218596157,
//          "first_name":"Mikhail",
//          "last_name":"Kraev",
//          "username":"gelugu",
//          "type":"private"
//        },
//      "date":1659529520,
//      "text":"\u0422\u0440\u0435\u0432\u043e\u0436\u043d\u043e"
//    }
//  },
//    {"update_id":119782898,"message":{"message_id":449,"from":{"id":412120748,"is_bot":false,"first_name":"Zlata","last_name":"Ulitina","username":"takoysyakoy","language_code":"en"},"chat":{"id":412120748,"first_name":"Zlata","last_name":"Ulitina","username":"takoysyakoy","type":"private"},"date":1659530164,"text":"/start",}},
//    {"update_id":119782899,"message":{"message_id":450,"from":{"id":412120748,"is_bot":false,"first_name":"Zlata","last_name":"Ulitina","username":"takoysyakoy","language_code":"en"},"chat":{"id":412120748,"first_name":"Zlata","last_name":"Ulitina","username":"takoysyakoy","type":"private"},"date":1659530178,"text":"/start","entities":[{"offset":0,"length":6,"type":"bot_command"}]}}
//  ]
//}
