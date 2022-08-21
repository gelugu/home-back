package com.gelugu.home.cache

import java.util.Date
import java.util.TimerTask

object InMemoryCache {
  val telegramCodes: HashMap<String, Pair<String, Date>> = hashMapOf()

  val timers: HashMap<String, TimerTask> = hashMapOf()
}