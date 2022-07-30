package com.gelugu.home.cache

import java.util.Date

object InMemoryCache {
  var code: String = ""
  var codeDate: Date = Date()

  var token: String = ""
  var tokenDate: Date = Date()

  var telegramToken: String = ""
  var telegramChatId: String = ""
}