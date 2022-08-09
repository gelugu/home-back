package com.gelugu.home.cache

import java.util.Date
import java.util.TimerTask

object InMemoryCache {
  var code: String = ""
  var codeDate: Date = Date()

  var telegramToken: String = ""
  var telegramChatId: String = ""

  val timers: HashMap<String, TimerTask> = hashMapOf()
}